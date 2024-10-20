package com.example.lottery.domain.lottery.controller

import com.example.lottery.domain.lottery.dto.LotteryMissionCallbackDto
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
        lotteryMissionService.createCompleteWaitingLotteryMissionRecord(dto)

        return ResponseEntity("Callback received successfully", HttpStatus.OK)
    }
}