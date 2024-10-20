package com.example.lottery.domain.lottery.repository

import com.example.lottery.domain.lottery.entity.LotteryMissionRecord
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LotteryMissionRecordRepository : JpaRepository<LotteryMissionRecord, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
    SELECT lmr 
    FROM LotteryMissionRecord lmr
    WHERE 
        lmr.mission.id = :missionId 
        AND lmr.user.id = :uid 
        AND lmr.status = 1
        AND lmr.completedAt BETWEEN :startMillis AND :endMillis
    """
    )
    fun findCompletedLotteryMissionRecordByStartAndEndAtMillis(missionId: Long, uid: String, startMillis: Long, endMillis: Long): List<LotteryMissionRecord>

    @Query("""
    SELECT lmr 
    FROM LotteryMissionRecord lmr 
        JOIN FETCH lmr.user u 
        JOIN FETCH lmr.mission m 
    WHERE lmr.status = 0 
        AND lmr.user.id = :uid 
        AND lmr.mission.id = :missionId 
        AND lmr.createdAt BETWEEN :startMillis AND :endMillis
    ORDER BY lmr.createdAt DESC
""")
    fun findWaitingMissionRecordByStartAndEndAtMillis(missionId: Long, uid: String, startMillis: Long, endMillis: Long): List<LotteryMissionRecord>

}