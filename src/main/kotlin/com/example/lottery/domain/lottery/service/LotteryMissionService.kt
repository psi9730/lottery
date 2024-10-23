package com.example.lottery.domain.lottery.service

import com.example.lottery.domain.lottery.dto.*
import com.example.lottery.domain.lottery.entity.LotteryMission
import com.example.lottery.domain.lottery.entity.LotteryMissionCoin
import com.example.lottery.domain.lottery.entity.LotteryMissionRecord
import com.example.lottery.domain.lottery.repository.LotteryMissionCoinRepository
import com.example.lottery.domain.lottery.repository.LotteryMissionRecordRepository
import com.example.lottery.domain.lottery.repository.LotteryMissionRepository
import com.example.lottery.domain.user.entity.User
import com.example.lottery.domain.user.service.UserService
import com.example.lottery.util.error.ResourceNotFoundException
import com.example.lottery.util.function.DateTimeUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LotteryMissionService(
    private val lotteryMissionRepository: LotteryMissionRepository,
    private val lotteryMissionRecordRepository: LotteryMissionRecordRepository,
    private val lotteryMissionCoinRepository: LotteryMissionCoinRepository,
    private val userService: UserService,
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
    fun getLotteryUser(uid: String): LotteryUserDto {
        val user = userService.findUserOrThrow(uid)

        return LotteryUserDto(uid, lotteryMissionCoinRepository.sumUserCoins(user.id) ?: 0)
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
    fun getLotteryMissionOfUser(uid: String): List<LotteryMissionDto> {
        userService.findUserOrThrow(uid)

        val missions = lotteryMissionRepository.findAll()
        val completedRecordsGrouped = findTodayCompletedRecords(uid).groupBy { it.mission.id }

        return missions.map { mission ->
            LotteryMissionDto(
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
            if (mission.type.isCompleteWaitingRequired()) {
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

    @Transactional
    fun completeLotteryMission(dto: CompleteLotteryMissionRequestDto): CompleteLotteryMissionResponseDto {
        try {
            val mission = lotteryMissionRepository.findByIdOrNull(dto.missionId)
                ?: throw ResourceNotFoundException("lottery mission is not found")

            val user = userService.findUserOrThrow(dto.uid)

            val today = DateTimeUtil.getTodayStartAndEndAt()

            mission.validateDailyCompletionLimit(
                lotteryMissionRecordRepository.countCompletedMissionsByDateRange(
                    missionId = dto.missionId,
                    uid = dto.uid,
                    startAt = today.start,
                    endAt = today.end,
                )
            )

            return CompleteLotteryMissionResponseDto(
                isSuccess = true,
                rewardedAmount = completeMissionWithReward(user, mission),
            )
        } catch (e: Exception) {
            return CompleteLotteryMissionResponseDto(
                isSuccess = false,
                rewardedAmount = 0,
                failedReason = e.message,
            )
        }
    }

    @Transactional
    fun createCompleteWaitingLotteryMissionRecord(dto: LotteryMissionCallbackDto) {
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