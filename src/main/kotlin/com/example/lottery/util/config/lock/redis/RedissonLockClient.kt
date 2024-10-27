package com.example.lottery.util.config.lock.redis

import com.example.lottery.util.config.lock.redis.dto.LockDto
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedissonLockService(
    private val redissonClient: RedissonClient
) {

    fun executeWithLock(lockParams: LockDto, task: () -> Unit) {
        val lock = redissonClient.getLock(lockParams.key)

        try {
            val isLockAcquired = lock.tryLock(lockParams.waitTime, lockParams.releaseTime, TimeUnit.SECONDS)
            if (isLockAcquired) {
                try {
                    task.invoke()
                } finally {
                    if (lock.isHeldByCurrentThread) {
                        lock.unlock()
                    }
                }
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}