package com.mhss.app.prayfirst.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val data: Map<String, List<MonthData>>,
)
