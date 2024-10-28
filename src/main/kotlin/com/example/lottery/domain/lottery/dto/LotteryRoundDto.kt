package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryRound

data class LotteryRoundDto (
    val round: Int,
    val winAnnounceAtMillis: Long,
    val numbers: LotteryNumbersDto?,
    val bonus: Int?,
) {
    companion object {
        fun of (
            lotteryRound: LotteryRound
        ) = LotteryRoundDto(
            round = lotteryRound.round,
            winAnnounceAtMillis = lotteryRound.drwNoDate.toEpochMilli(),
            numbers = lotteryRound.numbers?.let { LotteryNumbersDto.of(it) },
            bonus = lotteryRound.bonusNumber
        )
    }
}