package com.mhss.app.prayfirst

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mhss.app.prayfirst.data.sync.SyncPrayerTimesWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        instance = this

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncPrayerTimesWorker>(
            28, TimeUnit.DAYS,
            1, TimeUnit.DAYS
        )
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
        private lateinit var instance: Context
        fun getString(resId: Int): String = instance.getString(resId)
    }
}