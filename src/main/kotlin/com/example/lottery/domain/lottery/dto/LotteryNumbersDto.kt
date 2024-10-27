package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryNumbers

data class LotteryNumbersDto(
    val numbers: List<Int>
) {
    companion object {
        fun of(numbers: LotteryNumbers): LotteryNumbersDto
                = LotteryNumbersDto(numbers = numbers.toList())
    }
}