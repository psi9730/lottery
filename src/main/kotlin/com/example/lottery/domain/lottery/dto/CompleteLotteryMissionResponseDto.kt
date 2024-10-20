package com.example.lottery.domain.lottery.dto

data class CompleteLotteryMissionResponseDto(
    val isSuccess: Boolean,
    val rewardedAmount: Long,
    val failedReason: String? = null,
)
