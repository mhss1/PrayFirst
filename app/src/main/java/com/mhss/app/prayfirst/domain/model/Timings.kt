package com.mhss.app.prayfirst.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Timings constructor(
    @SerialName("Fajr")
    val fajr: String,
    @SerialName("Sunrise")
    val sunrise: String,
    @SerialName("Dhuhr")
    val dhuhr: String,
    @SerialName("Asr")
    val asr: String,
    @SerialName("Maghrib")
    val maghrib: String,
    @SerialName("Isha")
    val isha: String,
)