package com.mhss.app.prayfirst.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Date(
    val gregorian: Gregorian
)