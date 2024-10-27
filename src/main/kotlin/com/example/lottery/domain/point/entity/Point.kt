package com.example.lottery.domain.point.entity

import com.example.lottery.domain.user.entity.User
import com.example.lottery.util.error.BusinessValidationException
import jakarta.persistence.*

class Point private constructor (
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
    fun copy(
        id: Long = this.id,
        user: User = this.user,
        amountType: AmountType = this.amountType,
        amount: Long = this.amount
    ): Point {
        return Point(id, user, amountType, amount)
    }

    enum class AmountType {
        PLUS,
        MINUS,
    }

    companion object {
        fun reward(user: User, amount: Long): Point {
            return Point(
                amountType = AmountType.PLUS,
                user = user,
                amount = amount,
            )
        }

        fun consume(user: User, totalAmount: Long, amount: Long): Point {
            if (totalAmount < amount) {
                throw BusinessValidationException("${amount}를 사용하기엔 적립금이 부족합니다.")
            }

            return Point(
                amountType = AmountType.MINUS,
                user = user,
                amount = amount,
            )
        }
    }
}