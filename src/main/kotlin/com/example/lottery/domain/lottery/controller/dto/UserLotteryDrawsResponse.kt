package com.example.lottery.domain.lottery.controller.dto

import com.example.lottery.domain.lottery.dto.LotteryRoundDto
import com.example.lottery.domain.lottery.dto.LotteryUserDrawDto
import com.example.lottery.domain.lottery.dto.UserLotteryDrawsDto

data class UserLotteryDrawsResponse (
    val lotteryRound: LotteryRoundDto,
    val userDraws: List<LotteryUserDrawDto>,
    val preRound: Int?,
    val nextRound: Int?,
) {
    companion object {
        fun of (dto: UserLotteryDrawsDto) =
            UserLotteryDrawsResponse(
                dto.lotteryRound,
                dto.userDraws,
                dto.preRound,
                dto.nextRound,
            )
    }
}