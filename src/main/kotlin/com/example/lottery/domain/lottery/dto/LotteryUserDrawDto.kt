package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryUserDraw

data class LotteryUserDrawDto (
    val id: Long,
    val uid: String,
    val numbers: LotteryNumbersDto,
    val canReward: Boolean,
    val drawAtMillis: Long,
    val isWin: Boolean?,
    val winPlace: Int?,
) {
    companion object {
        fun of (lotteryUserDraw: LotteryUserDraw) =
            LotteryUserDrawDto (
                id = lotteryUserDraw.id,
                uid = lotteryUserDraw.user.id,
                numbers = LotteryNumbersDto.of(lotteryUserDraw.numbers),
                canReward = lotteryUserDraw.canReward,
                drawAtMillis = lotteryUserDraw.createdAt.toEpochMilli(),
                isWin = lotteryUserDraw.isWin,
                winPlace = lotteryUserDraw.winPlace,
            )
    }
}