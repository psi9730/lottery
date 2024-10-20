package com.example.lottery.domain.lottery.entity

import com.example.lottery.domain.user.entity.User
import com.example.lottery.util.error.BusinessValidationException
import com.example.lottery.util.function.DateTimeUtil
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "lottery_mission_records")
class LotteryMissionRecord (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lottery_mission_id", nullable = false)
    val mission: LotteryMission,

    @Column
    var status: Status,

    @Column
    val createdAt: Long = Instant.now().toEpochMilli(),

    @Column
    var completedAt: Long?
) {
    enum class Status {
        COMPLETE_WAITING,
        COMPLETED,
    }

    companion object {
        private const val expirationTime = 20000

        fun updateWaitingMissionToCompleted(mission: LotteryMissionRecord): LotteryMissionRecord {
            val currentTime = Instant.now().toEpochMilli()

            if (currentTime - mission.createdAt <= expirationTime) {
                mission.status = Status.COMPLETED
                mission.completedAt = currentTime
                return mission
            } else {
                throw BusinessValidationException("Mission record has expired and cannot be completed.")
            }
        }

        fun createCompleteWaitingMissionRecord(user: User, mission: LotteryMission, startAt: String): LotteryMissionRecord {
            if (DateTimeUtil.isOver24HoursFromUtcString(startAt)) {
                throw BusinessValidationException("startAt is over 24 hours")
            }

            if (LotteryMission.isCompleteWaitingRequiredType(mission.type)) {
                return LotteryMissionRecord(
                    user = user,
                    mission = mission,
                    status = Status.COMPLETE_WAITING,
                    completedAt = null,
                )
            } else {
                throw BusinessValidationException("${mission.type} is not allowed")
            }
        }

        fun createCompletedMissionRecord(user: User, mission: LotteryMission): LotteryMissionRecord {
            if (!LotteryMission.isCompleteWaitingRequiredType(mission.type)) {
                return LotteryMissionRecord(
                    user = user,
                    mission = mission,
                    status = Status.COMPLETED,
                    completedAt = Instant.now().toEpochMilli(),
                )
            } else {
                throw BusinessValidationException("${mission.type} can't complete directly")
            }
        }
    }
}