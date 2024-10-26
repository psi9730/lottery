package com.example.lottery

import com.example.lottery.domain.lottery.controller.dto.CompleteLotteryMissionRequestDto
import com.example.lottery.domain.lottery.controller.dto.LotteryMissionCreateDto
import com.example.lottery.domain.lottery.entity.LotteryMission
import com.example.lottery.domain.lottery.service.LotteryMissionService
import com.example.lottery.domain.user.controller.dto.CreateUserRequest
import com.example.lottery.domain.user.entity.User
import com.example.lottery.domain.user.service.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.PessimisticLockingFailureException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.test.assertTrue

@DisplayName("Optimistic Locking Test for Lottery Mission Completion")
@SpringBootTest
class LotteryMissionCompletionOptimisticLockTest {

    @Autowired
    private lateinit var lotteryMissionService: LotteryMissionService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `optimistic lock test for completing lottery mission`() {
        val mission = createLotteryMission()
        val user = createUser()

        val numberOfThreads = 3
        val executorService = Executors.newFixedThreadPool(numberOfThreads)

        val futures: List<Future<*>> = List(numberOfThreads) {
            executorService.submit {
                val dto = CompleteLotteryMissionRequestDto(missionId = mission.id, uid = user.id)
                lotteryMissionService.completeLotteryMission(dto)
            }
        }

        var result: Exception? = null

        try {
            futures.forEach { it.get() }  // Wait for all threads to complete
        } catch (e: ExecutionException) {
            println("hello")
            println(e)
            result = e.cause as Exception
        }

        assertTrue(result is PessimisticLockingFailureException)
    }

    private fun createLotteryMission(): LotteryMission {
        return lotteryMissionService.saveLotteryMission(
            LotteryMissionCreateDto(LotteryMission.MissionType.ATTENDANCE, 20, 1),
            )
    }

    private fun createUser(): User {
        return userService.saveUser(CreateUserRequest(user_name = "test", email="test@gmail.com", phoneNumber = "01096970444"))  // Save to database
    }
}