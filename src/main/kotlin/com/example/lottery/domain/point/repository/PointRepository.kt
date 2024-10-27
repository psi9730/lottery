package com.example.lottery.domain.point.repository

import com.example.lottery.domain.point.entity.Point

interface PointRepository {
    fun sumUserPoints(userId: String): Long
    fun findById(id: Long): Point?
    fun save(point: Point): Point
}
