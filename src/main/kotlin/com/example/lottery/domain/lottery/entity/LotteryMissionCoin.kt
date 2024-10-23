package com.example.lottery.domain.lottery.entity

import com.example.lottery.domain.user.entity.User
import jakarta.persistence.*
import kotlin.random.Random

@Entity
@Table(name = "lottery_mission_coins")
class LotteryMissionCoin (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val amountType: AmountType,

    @Column(nullable = false)
    val amount: Long,
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

        fun minusCoin(user: User, amount: Long): LotteryMissionCoin {
            return LotteryMissionCoin(
                amountType = AmountType.PLUS,
                user = user,
                amount = amount,
            )
        }
    }
}