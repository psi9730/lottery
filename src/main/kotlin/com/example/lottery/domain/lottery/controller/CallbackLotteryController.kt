package com.example.lottery.domain.lottery.controller

import com.example.lottery.domain.lottery.controller.dto.LotteryMissionCallbackDto
import com.example.lottery.domain.lottery.dto.CreateCompleteWaitingLotteryMissionDto
import com.example.lottery.domain.lottery.service.LotteryMissionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lotteries-callback")
class CallbackLotteryController (
    private val lotteryMissionService: LotteryMissionService
) {

    @PostMapping("/missions/complete")
    fun handleCompleteMissionCallback(@RequestBody dto: LotteryMissionCallbackDto): ResponseEntity<String> {
        lotteryMissionService.createCompleteWaitingLotteryMissionRecord(CreateCompleteWaitingLotteryMissionDto(
            dto.uid,
            dto.missionId,
            dto.startAt
        ))

        return ResponseEntity("Callback received successfully", HttpStatus.OK)
    }
}