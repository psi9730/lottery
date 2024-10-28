package com.example.lottery.domain.lottery.service

import com.example.lottery.domain.lottery.entity.LotteryRound
import com.example.lottery.domain.lottery.repository.LotteryRoundRepository
import com.example.lottery.util.config.lock.redis.RedissonLockService
import com.example.lottery.util.config.lock.redis.dto.LockDto
import com.example.lottery.util.error.ResourceNotFoundException
import com.example.lottery.util.function.DateTimeUtil.Companion.getStartAndEndOfWeek
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant

@Service
class LotteryRoundService (
    private val lotteryRoundRepository: LotteryRoundRepository,
    private val redissonLockService: RedissonLockService,
    private val transactionTemplate: TransactionTemplate,
    ){
    @Transactional(readOnly = true)
    fun findThisWeekLotteryRound(): LotteryRound? {
        val (startOfWeek, endOfWeek) = getStartAndEndOfWeek()
        return lotteryRoundRepository.findFirstByDrwNoDateBetween(startOfWeek, endOfWeek)
    }

    @Transactional(readOnly = true)
    fun getLotteryRound(round: Int): LotteryRound {
        return lotteryRoundRepository.findByRound(round) ?: throw ResourceNotFoundException("no LotteryRound $round exist")
    }

    @Transactional(readOnly = true)
    fun findCurrentLotteryRound(): LotteryRound? {
        return lotteryRoundRepository.findFirstByDrwNoDate(Instant.now())
    }

    @Transactional(readOnly = true)
    fun findLatestBeforeDate(): LotteryRound? {
        return lotteryRoundRepository.findLatestBeforeDate(Instant.now())
    }

    @Transactional
    fun saveLotteryRound(lotteryRound: LotteryRound): LotteryRound =
        lotteryRoundRepository.save(lotteryRound)

    @Transactional
    fun getCurrentLotteryRoundOrCreate(): LotteryRound {
        val thisWeekLotteryRound = findCurrentLotteryRound()

        return thisWeekLotteryRound ?: run {
            return redissonLockService.executeWithLock(
                LockDto(
                    key = "save-current-lottery-round-lock",
                )
            ) {
                transactionTemplate.execute { status ->
                    try {
                        return@execute saveLotteryRound(
                            LotteryRound.nextLotteryRound(
                                findLatestBeforeDate()
                            )
                        )
                    }  catch (ex: Exception) {
                        status.setRollbackOnly()
                        throw ex
                    }
                }
            } ?: throw IllegalStateException("Failed to acquire lock or save the lottery round.")
        }
    }
}