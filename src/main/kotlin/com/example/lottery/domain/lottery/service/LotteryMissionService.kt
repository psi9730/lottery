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
import com.example.lottery.util.error.BusinessValidationException
import com.example.lottery.util.error.ResourceNotFoundException
import com.example.lottery.util.function.DateTimeUtil
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LotteryMissionService(
    private val lotteryMissionRepository: LotteryMissionRepository,
    private val lotteryMissionRecordRepository: LotteryMissionRecordRepository,
    private val lotteryMissionCoinRepository: LotteryMissionCoinRepository,
    private val userService: UserService,
) {
    fun saveLotteryMission(dto: LotteryMissionCreateDto): LotteryMission {
        return lotteryMissionRepository.save(LotteryMission.fromDto(dto))
    }

    fun getLotteryUser(uid: String): LotteryUserDto {
        val user = userService.findUserOrThrow(uid)
        val coins = lotteryMissionCoinRepository.findAllByUser(user)

        val totalCoin = coins.fold(0L) { total, transaction ->
            when (transaction.amountType) {
                LotteryMissionCoin.AmountType.PLUS -> total + transaction.amount
                LotteryMissionCoin.AmountType.MINUS -> total - transaction.amount
            }
        }

        return LotteryUserDto(uid, totalCoin)
    }

    @Transactional
    fun getLotteryMissionOfUser(uid: String): List<LotteryMissionDto> {
        val missions = lotteryMissionRepository.findAll()
        val today = DateTimeUtil.getTodayStartAndEndAt()

        return missions.map { mission ->
            LotteryMissionDto(
                id = mission.id,
                type = mission.type,
                maxRewardAmount = mission.maxRewardAmount,
                maxDailyCount = mission.maxDailyCount,
                remainingDailyCount =
                mission.maxDailyCount - lotteryMissionRecordRepository.findCompletedLotteryMissionRecordByStartAndEndAtMillis(
                    mission.id,
                    uid,
                    today.start,
                    today.end,
                ).size
            )
        }

    }

    private fun updateWaitingMissionToCompleted(user: User, mission: LotteryMission): LotteryMissionRecord {
        val today = DateTimeUtil.getTodayStartAndEndAt()
        val waitingMissionRecord = lotteryMissionRecordRepository.findWaitingMissionRecordByStartAndEndAtMillis(mission.id, user.id, today.start, today.end).firstOrNull()

        return waitingMissionRecord?.let {
            lotteryMissionRecordRepository.save(LotteryMissionRecord.updateWaitingMissionToCompleted(it))
        } ?: throw ResourceNotFoundException("No waiting mission record found.")
    }

    private fun completeMissionWithReward(user: User, mission: LotteryMission): Long {
        if (LotteryMission.isCompleteWaitingRequiredType(mission.type)) {
            lotteryMissionRecordRepository.save(
                updateWaitingMissionToCompleted(
                    user,
                    mission,
                )
            )
        } else {
            lotteryMissionRecordRepository.save(
                LotteryMissionRecord.createCompletedMissionRecord(
                    user,
                    mission,
                )
            )
        }

        return lotteryMissionCoinRepository.save(
            LotteryMissionCoin.plusCoin(
                LotteryMissionCoinCreateDto(
                    user,
                    mission.maxRewardAmount,
                )
            )
        ).amount
    }

    @Transactional
    fun completeLotteryMission(dto: CompleteLotteryMissionRequestDto): CompleteLotteryMissionResponseDto {
        try {
            val today = DateTimeUtil.getTodayStartAndEndAt()

            val mission = lotteryMissionRepository.findByIdOrNull(dto.missionId)
                ?: throw ResourceNotFoundException("lottery mission is not found")

            val user = userService.findUserOrThrow(dto.uid)

            val todayCompleteLotteryMissions: List<LotteryMissionRecord> =
                lotteryMissionRecordRepository.findCompletedLotteryMissionRecordByStartAndEndAtMillis(
                    missionId = dto.missionId,
                    uid = dto.uid,
                    startMillis = today.start,
                    endMillis = today.end,
                )

            if (todayCompleteLotteryMissions.size >= mission.maxDailyCount) {
                throw BusinessValidationException("today complete lottery over")
            }

            val reward = completeMissionWithReward(user, mission)

            return CompleteLotteryMissionResponseDto(
                isSuccess = true,
                rewardedAmount = reward,
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