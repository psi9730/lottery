package com.example.lottery.domain.lottery.service

import com.example.lottery.domain.lottery.dto.DrawLotteryNumbersDto
import com.example.lottery.domain.lottery.dto.UserLotteryDrawsDto
import com.example.lottery.domain.lottery.entity.LotteryNumbers
import com.example.lottery.domain.lottery.entity.LotteryReward
import com.example.lottery.domain.lottery.entity.LotteryUserDraw
import com.example.lottery.domain.lottery.repository.LotteryUserDrawRepository
import com.example.lottery.domain.lotteryMission.service.LotteryMissionService
import com.example.lottery.domain.point.service.PointService
import com.example.lottery.domain.user.service.UserService
import com.example.lottery.util.config.lock.redis.RedissonLockService
import com.example.lottery.util.config.lock.redis.dto.LockDto
import com.example.lottery.util.error.ResourceNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@Service
class LotteryService(
    private val lotteryMissionService: LotteryMissionService,
    private val userService: UserService,
    private val lotteryUserDrawRepository: LotteryUserDrawRepository,
    private val lotteryRoundService: LotteryRoundService,
    private val pointService: PointService,
    private val redissonLockService: RedissonLockService,
    private val transactionTemplate: TransactionTemplate,
) {
    fun rewardLotteryDraw(uid: String, lotteryRound: Int, drawId: Long) {
        val draw = lotteryUserDrawRepository.findByIdOrNull(drawId) ?: throw ResourceNotFoundException("찾는 복권이 존재하지 않습니다.")
        if (draw.lotteryRound.round != lotteryRound) {
            throw IllegalArgumentException("잘못된 접근입니다.")
        }

        draw.validateReward()

        redissonLockService.executeWithLock(
            LockDto(
                key = "reward-lottery-draw:$uid",
            )
        ) {
            transactionTemplate.execute { status ->
                try {
                    val lotteryReward = LotteryReward(draw.winPlace)
                    if (lotteryReward.rewardType == LotteryReward.RewardType.Point) {
                        pointService.rewardPoint(uid, lotteryReward.reward)
                    }
                    lotteryUserDrawRepository.save(
                        draw.reward()
                    )
                } catch (ex: Exception) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } ?: throw IllegalStateException("Failed to acquire lock or save the lottery round.")

        return
    }

    @Transactional
    fun getCurrentLotteryDraws(uid: String): UserLotteryDrawsDto {
        val lotteryRound = lotteryRoundService.getCurrentLotteryRoundOrCreate()
        val userDraws = lotteryUserDrawRepository.findByUserAndRound(userId = uid, round = lotteryRound.round)

        return UserLotteryDrawsDto.of(lotteryRound, userDraws)
    }

    @Transactional(readOnly = true)
    fun getLotteryDraws(uid: String, round: Int): UserLotteryDrawsDto {
        val lotteryRound = lotteryRoundService.getLotteryRound(round)
        val userDraws = lotteryUserDrawRepository.findByUserAndRound(userId = uid, round = lotteryRound.round)

        return UserLotteryDrawsDto.of(lotteryRound, userDraws)
    }

    fun drawRandomLotteryNumbers(uid: String): DrawLotteryNumbersDto {
        val user = userService.findUserOrThrow(uid)

        val result: LotteryUserDraw = redissonLockService.executeWithLock(
            LockDto(
                key = "draw-random-lottery-numbers-lock:$uid",
            )
        ) {
            transactionTemplate.execute { status ->
                try {
                    lotteryMissionService.consumeCoin(uid, LotteryUserDraw.coinToDraw)
                    return@execute lotteryUserDrawRepository.save(
                        LotteryUserDraw.drawLotto(
                            lotteryRound = lotteryRoundService.getCurrentLotteryRoundOrCreate(),
                            numbers = LotteryNumbers.random(),
                            user = user,
                        )
                    )
                } catch (ex: Exception) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } ?: throw IllegalStateException("Failed to acquire lock or save the lottery round.")

        return DrawLotteryNumbersDto.of(result)
    }
}