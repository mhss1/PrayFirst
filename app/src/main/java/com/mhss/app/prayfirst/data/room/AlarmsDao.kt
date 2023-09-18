package com.mhss.app.prayfirst.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mhss.app.prayfirst.domain.model.Alarm

@Dao
interface AlarmsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAlarm(alarm: Alarm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAlarms(alarms: List<Alarm>)

    @Query("DELETE FROM alarms WHERE type = :type")
    suspend fun deleteAlarmByType(type: Int)

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarms(): List<Alarm>
}