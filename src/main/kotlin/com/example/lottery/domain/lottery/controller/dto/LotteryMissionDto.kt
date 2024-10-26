package com.example.lottery.domain.lottery.controller.dto

import com.example.lottery.domain.lottery.dto.LotteryMissionWithRemainingDailyCount
import com.example.lottery.domain.lottery.entity.LotteryMission

data class LotteryMissionDto (
    val id: Long,
    val type: LotteryMission.MissionType,
    val maxRewardAmount: Long,
    val maxDailyCount: Int,
    val remainingDailyCount: Int,
) {
    companion object {
        fun of(mission: LotteryMissionWithRemainingDailyCount): LotteryMissionDto {
            return LotteryMissionDto(mission.id, mission.type, mission.maxRewardAmount, mission.maxDailyCount, mission.remainingDailyCount)
        }
    }
}