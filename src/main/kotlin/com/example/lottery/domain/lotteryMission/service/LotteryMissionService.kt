package com.example.lottery.domain.lotteryMission.service

import com.example.lottery.domain.lotteryMission.dto.*
import com.example.lottery.domain.lotteryMission.entity.LotteryMission
import com.example.lottery.domain.lotteryMission.entity.LotteryMissionCoin
import com.example.lottery.domain.lotteryMission.entity.LotteryMissionRecord
import com.example.lottery.domain.lotteryMission.repository.LotteryMissionCoinRepository
import com.example.lottery.domain.lotteryMission.repository.LotteryMissionRecordRepository
import com.example.lottery.domain.lotteryMission.repository.LotteryMissionRepository
import com.example.lottery.domain.user.entity.User
import com.example.lottery.domain.user.service.UserService
import com.example.lottery.util.error.ResourceNotFoundException
import com.example.lottery.util.function.DateTimeUtil
import com.example.lottery.util.config.lock.redis.RedissonLockService
import com.example.lottery.util.config.lock.redis.dto.LockDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@Service
class LotteryMissionService(
    private val lotteryMissionRepository: LotteryMissionRepository,
    private val lotteryMissionRecordRepository: LotteryMissionRecordRepository,
    private val lotteryMissionCoinRepository: LotteryMissionCoinRepository,
    private val userService: UserService,
    private val redissonLockService: RedissonLockService,
    private val transactionTemplate: TransactionTemplate,
) {

    fun saveLotteryMission(dto: LotteryMissionCreateDto): LotteryMission {
        return lotteryMissionRepository.save(
            LotteryMission(
                type=dto.type,
                maxRewardAmount=dto.maxRewardAmount,
                maxDailyCount=dto.maxDailyCount
            )
        )
    }

    @Transactional(readOnly = true)
    fun getLotteryUser(uid: String): LotteryUser {
        val user = userService.findUserOrThrow(uid)

        return LotteryUser(uid, lotteryMissionCoinRepository.sumUserCoins(user.id) ?: 0)
    }

    private fun findTodayCompletedRecords(uid: String): List<LotteryMissionRecord> {
        val today = DateTimeUtil.getTodayStartAndEndAt()

        return lotteryMissionRecordRepository.findAllCompletedMissionsByDateRange(
            uid,
            today.start,
            today.end,
        )
    }

    @Transactional(readOnly = true)
    fun getLotteryMissionOfUser(uid: String): List<LotteryMissionWithRemainingDailyCount> {
        userService.findUserOrThrow(uid)

        val missions = lotteryMissionRepository.findAll()
        val completedRecordsGrouped = findTodayCompletedRecords(uid).groupBy { it.mission.id }

        return missions.map { mission ->
            LotteryMissionWithRemainingDailyCount(
                id = mission.id,
                type = mission.type,
                maxRewardAmount = mission.maxRewardAmount,
                maxDailyCount = mission.maxDailyCount,
                remainingDailyCount = mission.maxDailyCount - (completedRecordsGrouped[mission.id]?.size ?: 0)
            )
        }
    }

    private fun updateWaitingMissionToCompleted(user: User, mission: LotteryMission): LotteryMissionRecord {
        val today = DateTimeUtil.getTodayStartAndEndAt()
        val waitingMissionRecord = lotteryMissionRecordRepository.findWaitingMissionRecordByDateRange(mission.id, user.id, today.start, today.end).firstOrNull()
            ?: throw ResourceNotFoundException("No waiting mission record found.")

        waitingMissionRecord.updateWaitingMissionToCompleted()
        return waitingMissionRecord
    }

    private fun completeMissionWithReward(user: User, mission: LotteryMission): Long {
        val missionRecord =
            if (mission.type.isCompleteWaitingRequired) {
                updateWaitingMissionToCompleted(user, mission)
            } else {
                LotteryMissionRecord.createCompletedMissionRecord(
                    user,
                    mission,
                )
            }
        lotteryMissionRecordRepository.save(missionRecord)

        return lotteryMissionCoinRepository.save(
            LotteryMissionCoin.plusCoinWithRandom(
                user = user,
                maxAmount = mission.maxRewardAmount,
            )
        ).amount
    }

    fun completeLotteryMission(missionId: Long, uid: String): CompleteLotteryMissionDto {
        try {
            val mission = lotteryMissionRepository.findByIdOrNull(missionId)
                ?: throw ResourceNotFoundException("lottery mission is not found")

            val user = userService.findUserOrThrow(uid)

            val lockKey = "lottery-mission-lock:${mission.id}:$uid"
            val lockParams = LockDto(
                key = lockKey,
            )
            var result = CompleteLotteryMissionDto(isSuccess = false, rewardedAmount = 0)

            redissonLockService.executeWithLock(lockParams) {
                transactionTemplate.execute { status ->
                    try {
                        val today = DateTimeUtil.getTodayStartAndEndAt()

                        mission.validateDailyCompletionLimit(
                            lotteryMissionRecordRepository.countCompletedMissionsByDateRange(
                                missionId = missionId,
                                uid = uid,
                                startAt = today.start,
                                endAt = today.end,
                            )
                        )

                        result = CompleteLotteryMissionDto(
                            isSuccess = true,
                            rewardedAmount = completeMissionWithReward(user, mission),
                        )
                    } catch (ex: Exception) {
                        status.setRollbackOnly()
                        throw ex
                    }
                }
            }

            return result
        } catch (e: Exception) {
            return CompleteLotteryMissionDto(
                isSuccess = false,
                rewardedAmount = 0,
                failedReason = e.message,
            )
        }
    }

    @Transactional
    fun createCompleteWaitingLotteryMissionRecord(dto: CreateCompleteWaitingLotteryMissionDto) {
        val mission = lotteryMissionRepository.findByIdOrNull(dto.missionId)
            ?: throw ResourceNotFoundException("lottery mission is not found")

        val user = userService.findUserOrThrow(dto.uid)

        lotteryMissionRecordRepository.save(
            LotteryMissionRecord.createCompleteWaitingMissionRecord(
                user,
                mission,
                startAt = dto.startAt
            )
        )
    }
}