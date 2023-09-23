package com.mhss.app.prayfirst.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mhss.app.prayfirst.domain.repository.PrayerAlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Job())
    @Inject
    lateinit var alarms: PrayerAlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            scope.launch {
                try {
                    alarms.rescheduleAlarms()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}