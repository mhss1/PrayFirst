package com.mhss.app.prayfirst.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    val time: Long,
    @PrimaryKey(autoGenerate = false)
    val type: Int,
)
