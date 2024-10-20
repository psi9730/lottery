package com.example.lottery.domain.lottery.dto

data class LotteryMissionCallbackDto (
    val uid: String,
    val missionId: Long,
    val startAt: String,
)