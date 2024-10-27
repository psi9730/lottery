package com.example.lottery.domain.point.controller

import com.example.lottery.domain.point.service.PointService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/points")
class PointController(
    private val pointService: PointService
) {
    @PostMapping("/point/reward")
    fun rewardPoint(@RequestHeader("uid") uid: String, @RequestParam amount: Long) {
        pointService.rewardPoint(uid, amount)
    }

    @PostMapping("/point/consume")
    fun consumePoint(@RequestHeader("uid") uid: String, @RequestParam amount: Long) {
        pointService.consumePoint(uid, amount)
    }
}