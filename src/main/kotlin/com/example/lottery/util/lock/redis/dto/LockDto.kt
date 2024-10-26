package com.example.lottery.util.lock.redis.dto

data class LockDto (
    val key: String,
    val waitTime: Long = 5,
    val releaseTime: Long = 10,
)