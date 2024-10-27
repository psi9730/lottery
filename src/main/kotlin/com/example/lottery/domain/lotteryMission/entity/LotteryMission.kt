package com.example.lottery.domain.lotteryMission.entity

import com.example.lottery.util.error.BusinessValidationException
import jakarta.persistence.*

@Entity
@Table(name = "lottery_missions")
class LotteryMission (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    val type: MissionType,

    @Column(nullable = false)
    val maxRewardAmount: Long,

    @Column(nullable = false)
    val maxDailyCount: Int,
) {
    enum class MissionType {
        KAKAO_SHARE,
        ATTENDANCE,
        VISIT_COUPANG,
        WATCH_AD;
        val isCompleteWaitingRequired: Boolean get() {
            return this == KAKAO_SHARE || this == WATCH_AD
        }
    }

    fun validateDailyCompletionLimit (completedMissionSize: Int) {
        if (completedMissionSize >= this.maxDailyCount) {
            throw BusinessValidationException("today complete lottery limit over")
        }
    }
}