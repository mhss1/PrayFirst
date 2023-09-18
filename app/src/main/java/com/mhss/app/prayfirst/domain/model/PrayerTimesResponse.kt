package com.mhss.app.prayfirst.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimesResponse(
    @SerialName("data")
    val monthsData: List<MonthData>,
)