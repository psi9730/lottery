package com.example.lottery.domain.lotteryMission.repository

import com.example.lottery.domain.lotteryMission.entity.LotteryMission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LotteryMissionRepository : JpaRepository<LotteryMission, Long>