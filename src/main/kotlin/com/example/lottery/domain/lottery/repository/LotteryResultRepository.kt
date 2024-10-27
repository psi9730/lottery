package com.example.lottery.domain.lottery.repository

import com.example.lottery.domain.lottery.entity.LotteryResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LotteryResultRepository : JpaRepository<LotteryResult, Long> {
    fun findTopByOrderByRoundDesc(): LotteryResult?
}