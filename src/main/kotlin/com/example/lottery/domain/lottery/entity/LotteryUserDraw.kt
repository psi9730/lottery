package com.example.lottery.domain.lottery.entity

import com.example.lottery.domain.lottery.entity.converter.LottoNumberConverter
import com.example.lottery.domain.user.entity.User
import com.example.lottery.util.error.BusinessValidationException
import com.example.lottery.util.function.DateTimeUtil
import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime

@Table(name = "lottery_user_draws")
@Entity
class LotteryUserDraw private constructor (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lottery_round")
    val lotteryRound: LotteryRound,

    @Convert(converter = LottoNumberConverter::class)
    val numbers: LotteryNumbers,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var isRewarded: Boolean = false
    ) {

    private val isLotteryRoundNotOpened: Boolean get() =
        this.lotteryRound.numbers == null


    val winPlace:Int? get() {
        val lotteryRound = this.lotteryRound
        val matchCount = lotteryRound.numbers?.count(this.numbers)
        return if (matchCount == 0 || matchCount == null) null else LotteryNumbers.NUM_OF_LOTTO_NUMBERS - matchCount
    }

    val isWin:Boolean? get() {
        if (isLotteryRoundNotOpened) {
            return null
        }
        return this.winPlace !== null
    }

    val canReward: Boolean get() = this.isWin == true && !isRewarded

    fun validateReward() {
        if (isRewarded) {
            throw BusinessValidationException("두번 보상을 지급하지 않습니다.")
        }

        if (isWin == null) {
            throw BusinessValidationException("아직 로또 결과가 안나왔습니다.")
        }

        if (isWin == false) {
            throw BusinessValidationException("지급할 보상이 없습니다.")
        }
    }
    fun reward (): LotteryUserDraw {
        this.isRewarded = true
        return this
    }

    companion object {
        const val coinToDraw: Long = 1L
        private fun validateDrawLotto() {
            val zonedDateTime = Instant.now().atZone(DateTimeUtil.zoneId)

            val saturdayEvening = LocalTime.of(20, 0)

            val sundayMorning = LocalTime.of(6, 0)

            val isInvalidateToBuy = when (zonedDateTime.dayOfWeek) {
                DayOfWeek.SATURDAY -> {
                    zonedDateTime.toLocalTime().isAfter(saturdayEvening)
                }
                DayOfWeek.SUNDAY -> {
                    zonedDateTime.toLocalTime().isBefore(sundayMorning)
                }
                else -> {
                    false
                }
            }
            if (isInvalidateToBuy) {
                throw BusinessValidationException("토요일 오후 8시부터 일요일 오전 6시까지는 복권을 못삽니다.")
            }
        }

        fun drawLotto(
            numbers: LotteryNumbers,
            user: User,
            lotteryRound: LotteryRound,
        ): LotteryUserDraw {
            validateDrawLotto()

            return LotteryUserDraw(
                numbers = numbers,
                user = user,
                lotteryRound = lotteryRound,
            )
        }
    }
}
