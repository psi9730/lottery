package com.example.lottery.domain.lottery.entity

class LotteryReward(private val winPlace: Int?) {
    enum class RewardType {
        Point,
        Cash,
    }

    val reward: Long
        get() {
            return when (winPlace) {
                1 -> 1_000_000
                2 -> 200_000
                3 -> 1_000
                4 -> 200
                5 -> 30
                else -> 0
            }
        }

    val rewardType: RewardType?
        get() {
            return when (winPlace) {
                1 -> RewardType.Cash
                2 -> RewardType.Cash
                3 -> RewardType.Point
                4 -> RewardType.Point
                5 -> RewardType.Point
                else -> null
            }
        }
}