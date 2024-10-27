package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryUserDraw

data class DrawLotteryNumbersDto(
    val numbers: LotteryNumbersDto,
    val winAnnounceAtMillis: Long
) {
    companion object {
        fun of(userDraw: LotteryUserDraw)
            = DrawLotteryNumbersDto(
                numbers = LotteryNumbersDto.of(userDraw.numbers),
                winAnnounceAtMillis = userDraw.lotteryRound.drwNoDate.toEpochMilli()
            )
    }
}