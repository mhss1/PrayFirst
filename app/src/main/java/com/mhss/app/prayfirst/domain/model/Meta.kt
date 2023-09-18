package com.mhss.app.prayfirst.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)