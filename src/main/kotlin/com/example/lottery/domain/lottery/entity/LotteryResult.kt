package com.example.lottery.domain.lottery.entity

import com.example.lottery.domain.lottery.entity.converter.LottoNumberConverter
import com.example.lottery.util.function.DateTimeUtil
import jakarta.persistence.*
import java.time.Instant

@Table(name = "lottery_result", uniqueConstraints = [UniqueConstraint(columnNames = ["round", "drw_no_date", "numbers"])])

@Entity
class LotteryResult private constructor (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, updatable = false)
    val round: Int,

    @Column(nullable = false, updatable = false)
    val drwNoDate: Instant,

    @Column(nullable = false, updatable = false)
    val bonusNumber: Int,

    @Convert(converter = LottoNumberConverter::class)
    val numbers: LottoNumbers,

    @Column(nullable = false, updatable = false)
    val firstWinAmount: Long,

    @Column(nullable = false, updatable = false)
    val firstWinCount: Int,

    @Column(nullable = false, updatable = false)
    val totalSellAmount: Long,
) {
    fun isTodayLotteryResultAlreadyExist(drwNoDateToCheck: Instant): Boolean {
        val thisLotteryDate = DateTimeUtil.convertToLocalDate(drwNoDateToCheck)
        val lotteryDateToCheck = DateTimeUtil.convertToLocalDate(this.drwNoDate)

        return thisLotteryDate == lotteryDateToCheck
    }

    companion object {
        fun of(
            round: Int,
            drwNoDate: Instant,
            bonusNumber: Int,
            numbers: Collection<Int>,
            firstWinAmount: Long,
            firstWinCount: Int,
            totalSellAmount: Long,
        ): LotteryResult {
            return LotteryResult(
                round = round,
                drwNoDate = drwNoDate,
                bonusNumber = bonusNumber,
                numbers = LottoNumbers(numbers),
                firstWinAmount = firstWinAmount,
                firstWinCount = firstWinCount,
                totalSellAmount = totalSellAmount,
            )
        }
    }
}