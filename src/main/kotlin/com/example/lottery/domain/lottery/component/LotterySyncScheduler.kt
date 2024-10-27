package com.example.lottery.domain.lottery.component
import com.example.lottery.domain.lottery.service.LotterySyncService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class LotterySyncScheduler(
    private val lotterySyncService: LotterySyncService
) {
    @Scheduled(cron = "0 0/5 20 * * SAT", zone = "Asia/Seoul")
    @SchedulerLock(name = ID_BATCH, lockAtMostFor = "2m", lockAtLeastFor = "10s")
    fun syncLotteryNumbers() {
        lotterySyncService.syncLotteryNumbers()
    }

    companion object {
        private const val ID_BATCH = "lottery-sync"
    }
}
