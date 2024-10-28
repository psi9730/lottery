package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryRound
import com.example.lottery.util.function.DateTimeUtil

sealed class LotteryRoundResponse {
    data class Success(
        val totSellamnt: Long,
        val returnValue: String,
        val drwNoDate: String,
        val firstWinamnt: Long,
        val drwtNo1: Int,
        val drwtNo2: Int,
        val drwtNo3: Int,
        val drwtNo4: Int,
        val drwtNo5: Int,
        val drwtNo6: Int,
        val bnusNo: Int,
        val firstPrzwnerCo: Int,
        val firstAccumamnt: Long,
        val drwNo: Int
    ) : LotteryRoundResponse() {
        fun toLotteryRoundUpdateDto (): LotteryRoundUpdateDto = LotteryRoundUpdateDto(
            numbers = listOf(drwtNo1, drwtNo2, drwtNo3, drwtNo4, drwtNo5, drwtNo6),
            bonusNumber = this.bnusNo,
            drwNoDate = DateTimeUtil.convertStringToInstant(this.drwNoDate),
            firstWinAmount = this.firstWinamnt,
            firstWinCount = this.firstPrzwnerCo,
            totalSellAmount = this.totSellamnt,
        )

        fun toLotteryRound(round: Int): LotteryRound = LotteryRound.of(
            round,
            numbers = listOf(drwtNo1, drwtNo2, drwtNo3, drwtNo4, drwtNo5, drwtNo6),
            bonusNumber = this.bnusNo,
            drwNoDate = DateTimeUtil.convertStringToInstant(this.drwNoDate),
            firstWinAmount = this.firstWinamnt,
            firstWinCount = this.firstPrzwnerCo,
            totalSellAmount = this.totSellamnt,
        )
    }

    data class Failure(
        val returnValue: String
    ) : LotteryRoundResponse()
}