package com.example.lottery.domain.lottery.repository

import com.example.lottery.domain.lottery.entity.LotteryMissionCoin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LotteryMissionCoinRepository : JpaRepository<LotteryMissionCoin, Long> {
    @Query("""
        SELECT
            SUM(CASE 
                WHEN lmc.amountType = com.example.lottery.domain.lottery.entity.LotteryMissionCoin.AmountType.PLUS 
                THEN lmc.amount 
                ELSE 0 
                END)
            - 
            SUM(CASE 
                WHEN lmc.amountType = com.example.lottery.domain.lottery.entity.LotteryMissionCoin.AmountType.MINUS 
                THEN lmc.amount 
                ELSE 0 
                END)
        FROM LotteryMissionCoin lmc 
        WHERE lmc.user.id = :userId
    """)
    fun sumUserCoins(userId: String): Long?
}