package com.example.lottery.domain.lottery.entity

import java.util.SortedSet

@JvmInline
value class LotteryNumbers constructor (private val numbers: SortedSet<Int>) {
    init {
        require(numbers.size == NUM_OF_LOTTO_NUMBERS) { "중복을 제외한 ${NUM_OF_LOTTO_NUMBERS}개의 번호가 필요합니다." }
        require(numbers.all { it in VALID_RANGE }) { NUMBER_RANGE_ERROR_MESSAGE }
    }

    constructor(number: Collection<Int>) : this(number.toSortedSet())

    fun joinToString() = this.numbers.joinToString(DELIMITER)
    fun toList() = this.numbers.toList()

    fun count(other: LotteryNumbers) = this.numbers.count { it in other.numbers }

    companion object {
        private const val LOTTO_MIN_NUMBER = 1
        private const val LOTTO_MAX_NUMBER = 45

        const val NUM_OF_LOTTO_NUMBERS = 6

        private val VALID_RANGE: IntRange = LOTTO_MIN_NUMBER..LOTTO_MAX_NUMBER

        private const val DELIMITER = ","

        private const val NUMBER_RANGE_ERROR_MESSAGE =
            "로또 번호는 [$LOTTO_MIN_NUMBER ~ $LOTTO_MAX_NUMBER] 범위여야 합니다."

        fun random(): LotteryNumbers {
            val randomNumbers = (LOTTO_MIN_NUMBER..LOTTO_MAX_NUMBER).shuffled().take(NUM_OF_LOTTO_NUMBERS).toSortedSet()
            return LotteryNumbers(randomNumbers)
        }
    }
}
