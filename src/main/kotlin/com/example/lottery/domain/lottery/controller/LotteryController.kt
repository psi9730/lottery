package com.example.lottery.domain.lottery.controller

import com.example.lottery.domain.lottery.dto.CompleteLotteryMissionRequestDto
import com.example.lottery.domain.lottery.dto.CompleteLotteryMissionResponseDto
import com.example.lottery.domain.lottery.dto.LotteryMissionDto
import com.example.lottery.domain.lottery.dto.LotteryUserDto
import com.example.lottery.domain.lottery.service.LotteryMissionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lotteries")
class LotteryController(
    private val lotteryMissionService: LotteryMissionService
) {
    @GetMapping("/users/me")
    fun getLotteryUser(@RequestHeader("uid") uid: String): LotteryUserDto {
        return lotteryMissionService.getLotteryUser(uid)
    }

    @GetMapping("/missions")
    fun getLotteryMissions(@RequestHeader("uid") uid: String): List<LotteryMissionDto> {
        return lotteryMissionService.getLotteryMissionOfUser(uid)
    }

    @PostMapping("/missions/{missionId}/complete")
    fun completeLotteryMission(@PathVariable("missionId") missionId: Long, @RequestHeader("uid") uid: String): CompleteLotteryMissionResponseDto {
        return lotteryMissionService.completeLotteryMission(CompleteLotteryMissionRequestDto(missionId, uid))
    }
}