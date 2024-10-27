package com.example.lottery.domain.lotteryMission.controller.dto

data class LotteryMissionCallbackDto (
    val uid: String,
    val missionId: Long,
    val startAt: String,
)