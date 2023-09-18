package com.mhss.app.prayfirst.domain.alarm

interface AlarmsManager {

    fun scheduleAlarm(time: Long, prayerType: Int)

    fun cancelAlarm(prayerType: Int)
}