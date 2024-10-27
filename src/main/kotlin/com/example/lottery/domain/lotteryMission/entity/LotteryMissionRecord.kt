package com.example.lottery.domain.lotteryMission.entity

import com.example.lottery.domain.user.entity.User
import com.example.lottery.util.error.BusinessValidationException
import com.example.lottery.util.function.DateTimeUtil
import jakarta.persistence.*
import java.time.Duration
import java.time.Instant

@Entity
@Table(name = "lottery_mission_records")
class LotteryMissionRecord private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lottery_mission_id", nullable = false)
    val mission: LotteryMission,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Status,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column
    var completedAt: Instant?,
) {
    enum class Status {
        COMPLETE_WAITING,
        COMPLETED,
    }

    private fun checkExpiration() {
        val currentTime = Instant.now()

        val timeElapsed = Duration.between(this.createdAt, currentTime)

        if (timeElapsed > expirationTime) {
            throw BusinessValidationException("Mission record has expired and cannot be completed.")
        }
    }

    fun updateWaitingMissionToCompleted() {
        checkExpiration()

        this.status = Status.COMPLETED
        this.completedAt = Instant.now()
    }

    companion object {
        private val expirationTime = Duration.ofDays(1)

        fun createCompleteWaitingMissionRecord(user: User, mission: LotteryMission, startAt: String): LotteryMissionRecord {
            if (!mission.type.isCompleteWaitingRequired()) {
                throw BusinessValidationException("${mission.type} is not allowed")
            }

            if (DateTimeUtil.isOver24HoursFromUtcString(startAt)) {
                throw BusinessValidationException("startAt is over 24 hours")
            }

            return LotteryMissionRecord(
                user = user,
                mission = mission,
                status = Status.COMPLETE_WAITING,
                completedAt = null,
            )
        }

        fun createCompletedMissionRecord(user: User, mission: LotteryMission): LotteryMissionRecord {
            if (mission.type.isCompleteWaitingRequired()) {
                throw BusinessValidationException("${mission.type} can't complete directly")
            }

            return LotteryMissionRecord(
                user = user,
                mission = mission,
                status = Status.COMPLETED,
                completedAt = Instant.now(),
            )
        }
    }
}