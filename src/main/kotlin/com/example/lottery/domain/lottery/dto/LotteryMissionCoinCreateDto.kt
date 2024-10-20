package com.example.lottery.domain.lottery.dto

import com.example.lottery.domain.user.entity.User

data class LotteryMissionCoinCreateDto (
    val user: User,
    val amount: Long,
)