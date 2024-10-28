package com.example.lottery.domain.lottery.repository

import com.example.lottery.domain.lottery.entity.LotteryUserDraw
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LotteryUserDrawRepository : JpaRepository<LotteryUserDraw, Long> {
    @Query("SELECT ld FROM LotteryUserDraw ld JOIN FETCH ld.user u JOIN FETCH ld.lotteryRound r WHERE ld.user.id = :userId AND ld.lotteryRound.round = :round")
    fun findByUserAndRound(
        userId: String,
        round: Int
    ): List<LotteryUserDraw>
}