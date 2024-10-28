package com.example.lottery.domain.point.repository

import com.example.lottery.domain.point.entity.Point
import org.springframework.stereotype.Repository

@Repository
class InMemoryPointRepository : PointRepository {
    private val coins = mutableListOf<Point>()
    private var nextId: Long = 1

    override fun sumUserPoints(userId: String): Long {
        return coins.filter { it.user.id == userId }
            .sumOf { if (it.amountType == Point.AmountType.PLUS) it.amount else -it.amount }
    }

    override fun findById(id: Long): Point? {
        return coins.find { it.id == id }
    }

    override fun save(point: Point): Point {
        val coinToSave = point.copy(id = nextId++)
        coins.removeIf { it.id == coinToSave.id }
        coins.add(coinToSave)
        return coinToSave
    }
}