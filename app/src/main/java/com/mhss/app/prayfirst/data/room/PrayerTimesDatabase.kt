package com.mhss.app.prayfirst.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mhss.app.prayfirst.domain.model.Alarm
import com.mhss.app.prayfirst.domain.model.PrayerTimeEntity

@Database(entities = [PrayerTimeEntity::class, Alarm::class], version = 1, exportSchema = true)
abstract class PrayerTimesDatabase: RoomDatabase() {

    abstract fun prayerTimesDao(): PrayerTimesDao
    abstract fun alarmsDao(): AlarmsDao

    companion object {
        const val DATABASE_NAME = "prayer_times_database"
    }
}