package com.example.lottery.domain.lotteryMission.repository

import com.example.lottery.domain.lotteryMission.entity.LotteryMissionRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface LotteryMissionRecordRepository : JpaRepository<LotteryMissionRecord, Long> {
    @Query("""
        SELECT lmr 
        FROM LotteryMissionRecord lmr 
            JOIN FETCH lmr.user u 
            JOIN FETCH lmr.mission m 
        WHERE 
            lmr.user.id = :uid 
            AND lmr.status = "COMPLETED"
            AND lmr.completedAt >= :startAt AND lmr.completedAt <= :endAt
    """)
    fun findAllCompletedMissionsByDateRange(
        uid: String,
        startAt: Instant,
        endAt: Instant
    ): List<LotteryMissionRecord>

    @Query("""
        SELECT COUNT(lmr) 
        FROM LotteryMissionRecord lmr 
        WHERE lmr.mission.id = :missionId 
            AND lmr.user.id = :uid 
            AND lmr.status = 'COMPLETED'
            AND lmr.completedAt >= :startAt AND lmr.completedAt <= :endAt
    """)
    fun countCompletedMissionsByDateRange(
        missionId: Long,
        uid: String,
        startAt: Instant,
        endAt: Instant
    ): Int

    @Query("""
        SELECT lmr 
        FROM LotteryMissionRecord lmr 
            JOIN FETCH lmr.user u 
            JOIN FETCH lmr.mission m 
        WHERE lmr.status = "COMPLETE_WAITING"
            AND lmr.user.id = :uid 
            AND lmr.mission.id = :missionId 
            AND lmr.createdAt >= :startAt AND lmr.createdAt <= :endAt
        ORDER BY lmr.createdAt DESC
    """)
    fun findWaitingMissionRecordByDateRange(missionId: Long, uid: String, startAt: Instant, endAt: Instant): List<LotteryMissionRecord>

}