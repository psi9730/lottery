package com.example.lottery.domain.lottery.util

import com.example.lottery.domain.lottery.dto.LotteryMissionCreateDto
import com.example.lottery.domain.lottery.entity.LotteryMission
import com.example.lottery.domain.lottery.service.LotteryMissionService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(private val lotteryMissionService: LotteryMissionService) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val missions = listOf(
            LotteryMissionCreateDto(LotteryMission.MissionType.KAKAO_SHARE, 10, 1),
            LotteryMissionCreateDto(LotteryMission.MissionType.ATTENDANCE, 20, 2),
            LotteryMissionCreateDto(LotteryMission.MissionType.VISIT_COUPANG, 30, 3),
            LotteryMissionCreateDto(LotteryMission.MissionType.WATCH_AD, 40, 4)
        )

        missions.forEach { mission ->
            try {
                lotteryMissionService.saveLotteryMission(mission)
            } catch (e: Exception) {
                println("Failed to save mission: $mission. Error: ${e.message}")
            }
        }
    }
}