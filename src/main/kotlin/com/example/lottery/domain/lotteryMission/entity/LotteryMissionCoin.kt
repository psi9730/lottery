package com.example.lottery.domain.lotteryMission.entity

import com.example.lottery.domain.user.entity.User
import com.example.lottery.util.error.BusinessValidationException
import jakarta.persistence.*
import java.time.Instant
import kotlin.random.Random

@Entity
@Table(name = "lottery_mission_coins")
class LotteryMissionCoin private constructor (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val amountType: AmountType,

    @Column(nullable = false)
    val amount: Long,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
    ) {
    enum class AmountType {
        PLUS,
        MINUS,
    }

    companion object {
        fun plusCoinWithRandom(user: User, maxAmount: Long, minAmount: Long = 1): LotteryMissionCoin {
            val randomAmount = Random.nextLong(minAmount, maxAmount + 1)

            return LotteryMissionCoin(
                amountType = AmountType.PLUS,
                user = user,
                amount = randomAmount,
            )
        }

        fun consumeCoin(user: User, totalAmount: Long, amount: Long): LotteryMissionCoin {
            if (totalAmount < amount) {
                throw BusinessValidationException("${amount}를 사용하기엔 코인이 부족합니다.")
            }

            return LotteryMissionCoin(
                amountType = AmountType.MINUS,
                user = user,
                amount = amount,
            )
        }
    }
}