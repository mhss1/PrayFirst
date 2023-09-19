package com.mhss.app.prayfirst.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mhss.app.prayfirst.domain.alarm.AlarmsManager
import com.mhss.app.prayfirst.services.PrayFirstOverlayService

class PrayersAlarmsManager(private val context: Context): AlarmsManager {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleAlarm(
        time: Long,
        prayerType: Int
    ) {
        val alarmIntent = Intent(context, PrayFirstOverlayService::class.java)
        alarmIntent.putExtra("type", prayerType)
        val pendingIntent = PendingIntent.getService(context, prayerType, alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        }
    }

    override fun cancelAlarm(prayerType: Int) {
        val alarmIntent = Intent(context, PrayFirstOverlayService::class.java)
        val pendingIntent = PendingIntent.getService(context, prayerType, alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
    }

}