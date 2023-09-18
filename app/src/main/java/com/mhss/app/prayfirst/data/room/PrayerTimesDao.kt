package com.mhss.app.prayfirst.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.mhss.app.prayfirst.domain.model.PrayerTimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerTimesDao {

    @Insert
    suspend fun insertPrayerTimes(prayerTimes: List<PrayerTimeEntity>)

    @Query("SELECT * FROM prayer_times WHERE date = :date")
    fun getPrayerTimesByDateStream(date: String): Flow<List<PrayerTimeEntity>>

    @Query("SELECT * FROM prayer_times WHERE date = :date AND type = :type")
    suspend fun getPrayerTimeByDateAndType(date: String, type: Int): PrayerTimeEntity?

    @Query("DELETE FROM prayer_times")
    suspend fun deleteAllPrayerTimes()

    @Transaction
    suspend fun savePrayerTimes(prayerTimes: List<PrayerTimeEntity>) {
        deleteAllPrayerTimes()
        insertPrayerTimes(prayerTimes)
    }
}