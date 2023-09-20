package com.mhss.app.prayfirst.data.repository

import com.mhss.app.prayfirst.data.room.AlarmsDao
import com.mhss.app.prayfirst.data.room.PrayerTimesDao
import com.mhss.app.prayfirst.domain.alarm.AlarmsManager
import com.mhss.app.prayfirst.domain.model.Alarm
import com.mhss.app.prayfirst.domain.repository.PrayerAlarmRepository
import com.mhss.app.prayfirst.util.now
import com.mhss.app.prayfirst.util.toFormattedDate
import com.mhss.app.prayfirst.util.tomorrow

class PrayerAlarmRepositoryImpl(
    private val alarmsManager: AlarmsManager,
    private val alarmsDao: AlarmsDao,
    private val prayersDao: PrayerTimesDao
) : PrayerAlarmRepository {

    override suspend fun scheduleNewAlarm(type: Int) {
        val prayerTime = prayersDao.getPrayerTimeByDateAndType(
            now().toFormattedDate(),
            type
        )?.let {
            if (it.time > now()) {
                it
            } else {
                prayersDao.getPrayerTimeByDateAndType(
                    tomorrow().toFormattedDate(),
                    type
                )
            }
        }
        prayerTime?.let {
            alarmsDao.insertOrUpdateAlarm(Alarm(it.time, type))
            alarmsManager.scheduleAlarm(it.time, type)
        }
    }

    override suspend fun scheduleNextAlarm(type: Int) {
        val prayerTime = prayersDao.getPrayerTimeByDateAndType(
            tomorrow().toFormattedDate(),
            type
        )
        prayerTime?.let {
            alarmsDao.insertOrUpdateAlarm(Alarm(it.time, type))
            alarmsManager.scheduleAlarm(it.time, type)
        }
    }

    override suspend fun cancelAlarm(type: Int) {
        alarmsDao.deleteAlarmByType(type)
        alarmsManager.cancelAlarm(type)
    }

    override suspend fun rescheduleAlarms() {
        val alarms = alarmsDao.getAllAlarms()
        alarms.forEach {
            val time = if (it.time > now()) {
                it.time
            } else {
                prayersDao.getPrayerTimeByDateAndType(
                    tomorrow().toFormattedDate(),
                    it.type
                )?.time ?: return@forEach // continue
            }
            alarmsManager.scheduleAlarm(time, it.type)
            alarmsDao.insertOrUpdateAlarm(Alarm(time, it.type))
        }
    }


}