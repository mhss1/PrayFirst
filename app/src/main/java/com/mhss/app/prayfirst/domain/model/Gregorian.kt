package com.mhss.app.prayfirst.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Gregorian(
    val date: String,
    val format: String,
)