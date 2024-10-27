package com.example.lottery.util.function

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters


data class StartAndEnd(
    val start: Instant,
    val end: Instant
)

class DateTimeUtil {
    companion object {
        private const val timezone = "Asia/Seoul"
        val zoneId: ZoneId = ZoneId.of(timezone)

        fun convertInstantToLocalDateTime (instant: Instant): LocalDateTime =
            LocalDateTime.ofInstant(instant, zoneId)


        fun convertStringToInstant(dateString: String): Instant {
            val localDate = LocalDate.parse(dateString)

            val zonedDateTime = localDate.atStartOfDay(zoneId)

            return zonedDateTime.toInstant()
        }


        fun getTodayStartAndEndAt(): StartAndEnd {
            val today: LocalDate = LocalDate.now()

            val startOfToday: ZonedDateTime = today.atStartOfDay(zoneId)
            val startInstant: Instant = startOfToday.toInstant()

            val endOfToday: ZonedDateTime = today.plusDays(1).atStartOfDay(zoneId).minusNanos(1)
            val endInstant: Instant = endOfToday.toInstant()

            return StartAndEnd(startInstant, endInstant)
        }

        fun getStartAndEndOfWeek(): Pair<Instant, Instant> {
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endOfWeek = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant()
            return Pair(startOfWeek, endOfWeek)
        }

        fun isOver24HoursFromUtcString(startAt: String): Boolean {
            return try {
                val zonedDateTime =
                    ZonedDateTime.parse(startAt, DateTimeFormatter.ISO_ZONED_DATE_TIME)

                val startInstant = zonedDateTime.toInstant()
                val currentInstant = Instant.now()

                return ChronoUnit.HOURS.between(startInstant, currentInstant) > 24
            } catch (e: DateTimeParseException) {
                false
            }
        }

    }
}