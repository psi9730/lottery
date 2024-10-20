package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryMission

data class LotteryMissionDto (
    val id: Long,
    val type: LotteryMission.MissionType,
    val maxRewardAmount: Long,
    val maxDailyCount: Int,
    val remainingDailyCount: Int,
)