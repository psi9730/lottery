package com.example.lottery.util.function

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal fun generateId(prefix: String): String {
    val currentDateTime = ZonedDateTime.now()
    val dateString = currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    val uuid = UUID.randomUUID().toString()

    return "$prefix$dateString-$uuid"
}
