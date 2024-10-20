package com.example.lottery.domain.lottery.repository

import com.example.lottery.domain.lottery.entity.LotteryMissionCoin
import com.example.lottery.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface LotteryMissionCoinRepository : JpaRepository<LotteryMissionCoin, Long> {
    fun findAllByUser(user: User): List<LotteryMissionCoin>
}