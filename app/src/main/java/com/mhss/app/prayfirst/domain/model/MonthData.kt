package com.mhss.app.prayfirst.domain.model

import com.mhss.app.prayfirst.util.toMillis
import kotlinx.serialization.Serializable

@Serializable
data class MonthData(
    val date: Date,
    val meta: Meta,
    val timings: Timings
)

fun MonthData.toPrayerTimeEntities() = listOf(
    PrayerTimeEntity(
        date = date.gregorian.date,
        time = timings.fajr.toMillis(),
        type = PrayerTimeType.FAJR.ordinal
    ),
    PrayerTimeEntity(
        date = date.gregorian.date,
        time = timings.sunrise.toMillis(),
        type = PrayerTimeType.SUNRISE.ordinal
    ),
    PrayerTimeEntity(
        date = date.gregorian.date,
        time = timings.dhuhr.toMillis(),
        type = PrayerTimeType.ZUHR.ordinal
    ),
    PrayerTimeEntity(
        date = date.gregorian.date,
        time = timings.asr.toMillis(),
        type = PrayerTimeType.ASR.ordinal
    ),
    PrayerTimeEntity(
        date = date.gregorian.date,
        time = timings.maghrib.toMillis(),
        type = PrayerTimeType.MAGHRIB.ordinal
    ),
    PrayerTimeEntity(
        date = date.gregorian.date,
        time = timings.isha.toMillis(),
        type = PrayerTimeType.ISHA.ordinal
    )
)