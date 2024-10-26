package com.example.lottery.domain.lottery.controller.dto

import com.example.lottery.domain.lottery.dto.CompleteLotteryMissionDto

data class CompleteLotteryMissionResponseDto(
    val isSuccess: Boolean,
    val rewardedAmount: Long,
    val failedReason: String? = null,
) {
    companion object {
        fun of (dto: CompleteLotteryMissionDto): CompleteLotteryMissionResponseDto {
            return CompleteLotteryMissionResponseDto(dto.isSuccess, dto.rewardedAmount, dto.failedReason)
        }
    }
}
