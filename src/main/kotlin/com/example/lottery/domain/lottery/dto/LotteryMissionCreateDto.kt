package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryMission

data class LotteryMissionCreateDto (
    val type: LotteryMission.MissionType,
    val maxRewardAmount: Long,
    val maxDailyCount: Int,
)
