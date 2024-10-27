package com.example.lottery.util.config.lock.redis

import com.example.lottery.util.config.lock.redis.dto.LockDto
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedissonLockService(
    private val redissonClient: RedissonClient
) {

    fun <T> executeWithLock(lockParams: LockDto, task: () -> T): T? {
        val lock = redissonClient.getLock(lockParams.key)

        try {
            val isLockAcquired = lock.tryLock(lockParams.waitTime, lockParams.releaseTime, TimeUnit.SECONDS)
            if (isLockAcquired) {
                return try {
                    task.invoke() // Execute the task and return its result
                } finally {
                    if (lock.isHeldByCurrentThread) {
                        lock.unlock()
                    }
                }
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        return null // Return null if the lock was not acquired or an error occurred
    }
}