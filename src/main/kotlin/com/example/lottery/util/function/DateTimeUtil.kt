package com.example.lottery.util.function

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit


data class StartAndEnd(
    val start: Long,
    val end: Long
)

class DateTimeUtil {
    companion object {
        private const val timezone = "Asia/Seoul"

        fun getTodayStartAndEndAt(): StartAndEnd {
            val zoneId = ZoneId.of(timezone)

            val today: LocalDate = LocalDate.now(zoneId)

            val startOfDay: LocalDateTime = today.atStartOfDay()
            val startOfDayInstant: Instant = startOfDay.atZone(zoneId).toInstant()

            val endOfDay: LocalDateTime = today.atTime(23, 59, 59, 999_999_999)
            val endOfDayInstant: Instant = endOfDay.atZone(zoneId).toInstant()

            return StartAndEnd(startOfDayInstant.toEpochMilli(), endOfDayInstant.toEpochMilli())
        }

        fun isOver24HoursFromUtcString(startAt: String): Boolean {
            return try {
                val zonedDateTime =
                    ZonedDateTime.parse(startAt, DateTimeFormatter.ISO_ZONED_DATE_TIME)

                val startInstant = zonedDateTime.toInstant()
                val currentInstant = Instant.now()

                ChronoUnit.HOURS.between(startInstant, currentInstant) > 24
            } catch (e: DateTimeParseException) {
                println("Hello")
                false
            }
        }

    }
}