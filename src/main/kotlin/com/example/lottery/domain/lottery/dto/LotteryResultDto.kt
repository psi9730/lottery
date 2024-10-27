package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryResult
import com.example.lottery.util.function.DateTimeUtil

sealed class LotteryResultDto {
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
    ) : LotteryResultDto() {
        fun toLotteryResult(round: Int): LotteryResult = LotteryResult.of(
            round,
            numbers = listOf(drwtNo1, drwtNo2, drwtNo3, drwtNo4, drwtNo5, drwtNo6),
            bonusNumber = this.bnusNo,
            drwNoDate = DateTimeUtil.convertToInstant(this.drwNoDate),
            firstWinAmount = this.firstWinamnt,
            firstWinCount = this.firstPrzwnerCo,
            totalSellAmount = this.totSellamnt,
        )
    }

    data class Failure(
        val returnValue: String
    ) : LotteryResultDto()
}