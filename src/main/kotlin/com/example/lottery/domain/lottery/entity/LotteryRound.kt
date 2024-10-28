package com.example.lottery.domain.lottery.entity

import com.example.lottery.domain.lottery.entity.converter.LottoNumberConverter
import com.example.lottery.util.function.DateTimeUtil
import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.temporal.TemporalAdjusters

@Table(name = "lottery_rounds", uniqueConstraints = [UniqueConstraint(columnNames = ["round"])])

@Entity
class LotteryRound private constructor (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val round: Int,

    @Column(nullable = false)
    var drwNoDate: Instant,

    @Column
    var bonusNumber: Int?,

    @Convert(converter = LottoNumberConverter::class)
    var numbers: LotteryNumbers?,

    @Column
    var firstWinAmount: Long?,

    @Column
    var firstWinCount: Int?,

    @Column
    var totalSellAmount: Long?,
) {
    val isLotteryRoundSynced: Boolean get() {
        return this.numbers != null
    }

    val nextRound: Int get() {
        return round + 1
    }

    val prevRound: Int get() {
        return round - 1
    }

    fun sync (
       numbers: Collection<Int>,
       bonusNumber: Int,
       drwNoDate: Instant,
       firstWinCount: Int,
       firstWinAmount: Long,
       totalSellAmount: Long,
    ) {
        this.numbers = LotteryNumbers(numbers)
        this.bonusNumber = bonusNumber
        this.drwNoDate = drwNoDate
        this.firstWinCount = firstWinCount
        this.firstWinAmount = firstWinAmount
        this.totalSellAmount = totalSellAmount
    }
    companion object {
        private fun nextRoundDrwNoDate (drwNoDate: Instant): Instant {
            val createdAtLocalDateTime = DateTimeUtil.convertInstantToLocalDateTime(drwNoDate)

            val nextSaturday = createdAtLocalDateTime.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
            val nextSaturdayAtNine = nextSaturday.withHour(9).withMinute(0).withSecond(0).withNano(0)

            return nextSaturdayAtNine.atZone(DateTimeUtil.zoneId).toInstant()
        }

        fun nextLotteryRound(
            prevRound: LotteryRound?,
        ): LotteryRound {
            if (prevRound == null) {
                return LotteryRound(
                    round = 0,
                    drwNoDate = nextRoundDrwNoDate(Instant.now()),
                    bonusNumber = null,
                    numbers = null,
                    firstWinAmount = null,
                    firstWinCount = null,
                    totalSellAmount = null,
                )
            }

            return LotteryRound(
                round = prevRound.nextRound,
                drwNoDate = nextRoundDrwNoDate(prevRound.drwNoDate),
                bonusNumber = null,
                numbers = null,
                firstWinAmount = null,
                firstWinCount = null,
                totalSellAmount = null,
            )
        }

        fun of(
            round: Int,
            drwNoDate: Instant,
            bonusNumber: Int,
            numbers: Collection<Int>,
            firstWinAmount: Long,
            firstWinCount: Int,
            totalSellAmount: Long,
        ): LotteryRound {
            return LotteryRound(
                round = round,
                drwNoDate = drwNoDate,
                bonusNumber = bonusNumber,
                numbers = LotteryNumbers(numbers),
                firstWinAmount = firstWinAmount,
                firstWinCount = firstWinCount,
                totalSellAmount = totalSellAmount,
            )
        }
    }
}