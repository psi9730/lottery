package com.example.lottery.domain.lottery.dto

import java.time.Instant

data class LotteryRoundUpdateDto (
    val drwNoDate: Instant,
    val bonusNumber: Int,
    val numbers: Collection<Int>,
    val firstWinAmount: Long,
    val firstWinCount: Int,
    val totalSellAmount: Long,
)