package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.lottery.entity.LotteryRound
import com.example.lottery.domain.lottery.entity.LotteryUserDraw

data class UserLotteryDrawsDto (
    val lotteryRound: LotteryRoundDto,
    val userDraws: List<LotteryUserDrawDto>,
    val preRound: Int?,
    val nextRound: Int?,
) {
    companion object {
        fun of(
            lotteryRound: LotteryRound,
            lotteryUserDraws: List<LotteryUserDraw>
        ): UserLotteryDrawsDto =
            UserLotteryDrawsDto(
                lotteryRound = LotteryRoundDto.of (
                    lotteryRound,
                ),
                userDraws = lotteryUserDraws.map { LotteryUserDrawDto.of(lotteryUserDraw = it) },
                preRound = lotteryRound.prevRound,
                nextRound = if (lotteryRound.isLotteryRoundSynced) lotteryRound.nextRound else null,
            )
    }
}