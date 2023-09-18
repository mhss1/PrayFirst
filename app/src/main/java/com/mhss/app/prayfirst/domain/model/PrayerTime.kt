package com.mhss.app.prayfirst.domain.model

data class PrayerTime(
    val timeFormatted: String,
    val time: Long,
    val nameResId: Int,
    val type: PrayerTimeType
)