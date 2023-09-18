package com.mhss.app.prayfirst.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mhss.app.prayfirst.domain.repository.PrayerAlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Job())
    @Inject lateinit var alarms: PrayerAlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
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