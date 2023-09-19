package com.mhss.app.prayfirst.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toMillis(): Long {
    return try {
        val dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault())
        val result = OffsetDateTime.parse(this.substringBefore("(").trim(), dateFormatter)
        result.toInstant().toEpochMilli()
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}


fun Long.toFormattedTime(): String {
    val dateFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    return dateFormat.format(
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneId.systemDefault()
        )
    )
}

fun Long.toFormattedDate(): String {
    val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    return dateFormat.format(
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneId.systemDefault()
        )
    )
}

fun getCurrentMonthWithYear(): Pair<String, String> {
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH)
    val monthFormatter = DateTimeFormatter.ofPattern("MM", Locale.ENGLISH)
    val time = LocalDateTime.now()
    return monthFormatter.format(time) to yearFormatter.format(time)
}

fun getNextMonthWithYear(): Pair<String, String> {
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH)
    val monthFormatter = DateTimeFormatter.ofPattern("MM", Locale.ENGLISH)
    val time = LocalDateTime.now().plusMonths(1)
    return monthFormatter.format(time) to yearFormatter.format(time)
}

fun tomorrow(): Long =
    LocalDateTime
        .now()
        .plusDays(1)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

fun yesterday(): Long =
    LocalDateTime
        .now()
        .minusDays(1)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

fun Long.isToday(): Boolean {
    val currentLocalDateTime = LocalDateTime.now()

    val timeLocalDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.systemDefault()
    )
    return currentLocalDateTime.dayOfYear == timeLocalDateTime.dayOfYear
}

fun isFriday(): Boolean {
    val localDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(System.currentTimeMillis()),
        ZoneId.systemDefault()
    )
    return localDateTime.dayOfWeek.value == DayOfWeek.FRIDAY.value
}

fun Long.formatTimerTime(): String {
    val localDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.of("UTC")
    )
    val pattern = if (localDateTime.hour > 0)
        "HH:mm:ss"
    else
        "mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return localDateTime.format(formatter)
}