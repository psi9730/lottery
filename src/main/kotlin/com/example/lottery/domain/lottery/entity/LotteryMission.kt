package com.example.lottery.domain.lottery.entity

import com.example.lottery.domain.lottery.dto.LotteryMissionCreateDto
import jakarta.persistence.*

@Entity
@Table(name = "lottery_missions")
class LotteryMission private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

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
        WATCH_AD
    }

    companion object {
        fun isCompleteWaitingRequiredType (type: MissionType): Boolean {
            return type == MissionType.KAKAO_SHARE || type == MissionType.WATCH_AD
        }

        fun fromDto(dto: LotteryMissionCreateDto): LotteryMission {
            return LotteryMission(
                type = dto.type,
                maxRewardAmount = dto.maxRewardAmount,
                maxDailyCount = dto.maxDailyCount,
            )
        }
    }
}