package com.example.lottery.domain.lottery.controller.dto

import com.example.lottery.domain.lottery.dto.LotteryUser

data class LotteryUserDto(
    val uid: String,
    val totalCoin: Long,
) {
    companion object {
        fun of (user: LotteryUser): LotteryUserDto {
            return LotteryUserDto(user.uid, user.totalCoin)
        }
    }
}
