package com.mhss.app.prayfirst.domain.repository

interface PrayerAlarmRepository {

    suspend fun scheduleNewAlarm(type: Int)

    suspend fun scheduleNextAlarm(type: Int)

    suspend fun cancelAlarm(type: Int)

    suspend fun rescheduleAlarms()
}