package com.example.lottery.domain.lotteryMission.dto

import com.example.lottery.domain.lotteryMission.entity.LotteryMission

data class LotteryMissionCreateDto (
    val type: LotteryMission.MissionType,
    val maxRewardAmount: Long,
    val maxDailyCount: Int,
)
