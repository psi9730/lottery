package com.example.lottery.domain.lottery.repository

import com.example.lottery.domain.lottery.entity.LotteryRound
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface LotteryRoundRepository : JpaRepository<LotteryRound, Long> {
    @Query("SELECT lr FROM LotteryRound lr WHERE lr.drwNoDate >= :startAt AND lr.drwNoDate < :endAt ORDER BY lr.drwNoDate ASC")
    fun findFirstByDrwNoDateBetween(startAt: Instant, endAt: Instant): LotteryRound?

    @Query("SELECT lr FROM LotteryRound lr WHERE lr.drwNoDate >= :startAt ORDER BY lr.drwNoDate ASC")
    fun findFirstByDrwNoDate(startAt: Instant): LotteryRound?

    @Query("SELECT lr FROM LotteryRound lr WHERE lr.drwNoDate < :startAt ORDER BY lr.drwNoDate DESC")
    fun findLatestBeforeDate(startAt: Instant): LotteryRound?

    fun findByRound(round: Int): LotteryRound?
}