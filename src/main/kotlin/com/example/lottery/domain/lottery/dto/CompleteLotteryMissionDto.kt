package com.example.lottery.domain.lottery.dto

data class CompleteLotteryMissionDto(
    val isSuccess: Boolean,
    val rewardedAmount: Long,
    val failedReason: String? = null,
)
