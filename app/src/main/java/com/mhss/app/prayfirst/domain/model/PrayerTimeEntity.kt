package com.mhss.app.prayfirst.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mhss.app.prayfirst.R
import com.mhss.app.prayfirst.util.isFriday
import com.mhss.app.prayfirst.util.toFormattedTime

@Entity(tableName = "prayer_times")
data class PrayerTimeEntity(
    val date: String,
    val time: Long,
    val type: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

fun PrayerTimeEntity.toPrayerTime(): PrayerTime {
    return PrayerTime(
        timeFormatted = time.toFormattedTime(),
        time = time,
        nameResId = when(type) {
            PrayerTimeType.FAJR.ordinal -> R.string.fajr
            PrayerTimeType.SUNRISE.ordinal -> R.string.sunrise
            PrayerTimeType.ZUHR.ordinal -> if (isFriday()) R.string.jumuaa else R.string.zuhr
            PrayerTimeType.ASR.ordinal -> R.string.asr
            PrayerTimeType.MAGHRIB.ordinal -> R.string.maghrib
            PrayerTimeType.ISHA.ordinal -> R.string.isha
            else -> R.string.fajr
        },
        type = PrayerTimeType.values()[type]
    )
}

enum class PrayerTimeType {
    FAJR,
    SUNRISE,
    ZUHR,
    ASR,
    MAGHRIB,
    ISHA
}
