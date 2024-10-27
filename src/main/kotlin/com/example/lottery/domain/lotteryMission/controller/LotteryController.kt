package com.example.lottery.domain.lotteryMission.controller

import com.example.lottery.domain.lotteryMission.controller.dto.CompleteLotteryMissionResponseDto
import com.example.lottery.domain.lotteryMission.controller.dto.LotteryMissionDto
import com.example.lottery.domain.lotteryMission.controller.dto.LotteryUserDto
import com.example.lottery.domain.lotteryMission.service.LotteryMissionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lotteries")
class LotteryController(
    private val lotteryMissionService: LotteryMissionService
) {
    @GetMapping("/users/me")
    fun getLotteryUser(@RequestHeader("uid") uid: String): LotteryUserDto {
        return LotteryUserDto.of(lotteryMissionService.getLotteryUser(uid))
    }

    @GetMapping("/missions")
    fun getLotteryMissions(@RequestHeader("uid") uid: String): List<LotteryMissionDto> {
        return lotteryMissionService.getLotteryMissionOfUser(uid).map(LotteryMissionDto.Companion::of)
    }

    @PostMapping("/missions/{missionId}/complete")
    fun completeLotteryMission(@PathVariable("missionId") missionId: Long, @RequestHeader("uid") uid: String): CompleteLotteryMissionResponseDto {
        return CompleteLotteryMissionResponseDto.of(lotteryMissionService.completeLotteryMission(missionId, uid))
    }
}