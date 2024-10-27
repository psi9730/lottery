package com.example.lottery.domain.point.service

import com.example.lottery.domain.point.entity.Point
import com.example.lottery.domain.point.repository.PointRepository
import com.example.lottery.domain.user.service.UserService
import com.example.lottery.util.config.lock.redis.RedissonLockService
import com.example.lottery.util.config.lock.redis.dto.LockDto
import com.example.lottery.util.error.BusinessValidationException
import com.example.lottery.util.error.ResourceNotFoundException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@Service
class PointService(
    private val pointRepository: PointRepository,
    private val userService: UserService,
    private val redissonLockService: RedissonLockService,
    private val transactionTemplate: TransactionTemplate,
    ) {

    @Retryable(
        value = [ResourceNotFoundException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 300L)
    )
    @Transactional
    fun rewardPoint (uid: String, amount: Long) {
        pointRepository.save(
            Point.reward(userService.findUserOrThrow(uid), amount)
        )
    }

    @Retryable(
        value = [ResourceNotFoundException::class, BusinessValidationException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 300L)
    )
    @Transactional
    fun consumePoint (uid: String, amount: Long) {
        val user = userService.findUserOrThrow(uid)
        redissonLockService.executeWithLock(
            LockDto(
                key = "consume-point:$uid",
            )
        ) {
            transactionTemplate.execute { status ->
                try {
                    val totalAmount = pointRepository.sumUserPoints(uid)
                    pointRepository.save(
                        Point.consume(user, totalAmount, amount)
                    )
                } catch (ex: Exception) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } ?: throw IllegalStateException("Failed to acquire lock or save the lottery round.")
    }
}