package com.mhss.app.prayfirst.util

import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
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
        .withDecimalStyle(DecimalStyle.ofDefaultLocale())
    return dateFormat.format(
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneId.systemDefault()
        )
    )
}

fun Long.toFormattedDate(): String {
    val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)
        .withDecimalStyle(DecimalStyle.of(Locale.ENGLISH))
    return dateFormat.format(
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneId.systemDefault()
        )
    )
}

fun getCurrentMonthWithYear(): Pair<String, String> {
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH)
        .withDecimalStyle(DecimalStyle.of(Locale.ENGLISH))
    val monthFormatter = DateTimeFormatter.ofPattern("MM", Locale.ENGLISH)
        .withDecimalStyle(DecimalStyle.of(Locale.ENGLISH))
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
    ZonedDateTime
        .now()
        .plusDays(1)
        .toInstant()
        .toEpochMilli()

fun yesterday(): Long =
    ZonedDateTime
        .now()
        .minusDays(1)
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
    val localDateTime = LocalDateTime.now()
    return localDateTime.dayOfWeek.value == DayOfWeek.FRIDAY.value
}

fun now() = Instant.now().toEpochMilli()

fun Long.formatTimerTime(): String {
    val duration = Duration.ofMillis(this)
    return if (duration.toHours() > 0) {
        String.format(
            "%02d:%02d:%02d",
            duration.toHours(),
            duration.toMinutes() % 60,
            duration.seconds % 60
        )
    } else {
        String.format(
            "%02d:%02d",
            duration.toMinutes() % 60,
            duration.seconds % 60
        )
    }
}