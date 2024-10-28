package com.example.lottery.domain.lottery.controller

import com.example.lottery.domain.lottery.controller.dto.DrawLotteryNumberResponse
import com.example.lottery.domain.lottery.controller.dto.UserLotteryDrawsResponse
import com.example.lottery.domain.lottery.service.LotteryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lotteries")
class LotteryController(
    private val lotteryService: LotteryService
) {
    @GetMapping("/current/users/me/draws")
    fun getCurrentLotteryDraws(@RequestHeader("uid") uid: String): UserLotteryDrawsResponse {
        return UserLotteryDrawsResponse.of(lotteryService.getCurrentLotteryDraws(uid))
    }

    @PostMapping("/current/users/me/draws")
    fun drawRandomLotteryNumbers(@RequestHeader("uid") uid: String): DrawLotteryNumberResponse {
        return DrawLotteryNumberResponse.of(lotteryService.drawRandomLotteryNumbers(uid))
    }

    @GetMapping("/{lotteryRound}/users/me/draws")
    fun getLotteryDraws(@RequestHeader("uid") uid: String, @PathVariable("lotteryRound") lotteryRound: Int): UserLotteryDrawsResponse {
        return UserLotteryDrawsResponse.of(lotteryService.getLotteryDraws(uid, lotteryRound))
    }

    @PostMapping("/{lotteryRound}/users/me/draws/confirm")
    fun confirmLotteryDraws(@RequestHeader("uid") uid: String, @PathVariable("lotteryRound") lotteryRound: String) {}

    @PostMapping("/{lotteryRound}/users/me/draws/{drawId}/reward")
    fun rewardLotteryDraw(@RequestHeader("uid") uid: String, @PathVariable("lotteryRound") lotteryRound: Int, @PathVariable("drawId") drawId: Long) {
        return lotteryService.rewardLotteryDraw(uid, lotteryRound, drawId)
    }
}