package com.example.lottery.domain.lottery.repository

import com.example.lottery.domain.lottery.entity.LotteryMission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LotteryMissionRepository : JpaRepository<LotteryMission, Long>