package com.example.lottery.domain.lottery.controller.dto

import com.example.lottery.domain.lottery.dto.DrawLotteryNumbersDto
import com.example.lottery.domain.lottery.dto.LotteryNumbersDto

data class DrawLotteryNumberResponse (
    val numbers: LotteryNumbersDto,
    val winAnnounceAtMillis: Long,
) {
    companion object {
        fun of (dto: DrawLotteryNumbersDto) =
            DrawLotteryNumberResponse(dto.numbers, dto.winAnnounceAtMillis)
    }
}