package com.example.lottery.domain.lottery.entity

import com.example.lottery.domain.lottery.dto.LotteryMissionCoinCreateDto
import com.example.lottery.domain.user.entity.User
import jakarta.persistence.*

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
        fun plusCoin(dto: LotteryMissionCoinCreateDto): LotteryMissionCoin {
            return LotteryMissionCoin(
                user = dto.user,
                amountType = AmountType.PLUS,
                amount = dto.amount,
            )
        }

        fun minusCoin(dto: LotteryMissionCoinCreateDto): LotteryMissionCoin {
            return LotteryMissionCoin(
                user = dto.user,
                amountType = AmountType.PLUS,
                amount = dto.amount,
            )
        }
    }
}