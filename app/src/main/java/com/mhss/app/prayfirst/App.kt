package com.mhss.app.prayfirst

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mhss.app.prayfirst.data.sync.SyncPrayerTimesWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncPrayerTimesWorker>(28, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresDeviceIdle(true)
                    .setRequiresCharging(true)
                    .build()
            ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "syncPrayerTimes",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Context
            private set
        fun getString(resId: Int): String = instance.getString(resId)
    }
}