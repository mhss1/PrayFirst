package com.mhss.app.prayfirst.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mhss.app.prayfirst.data.repository.DataStoreRepository
import com.mhss.app.prayfirst.domain.repository.PrayerTimesRepository
import com.mhss.app.prayfirst.presentation.main.LoadingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncPrayerTimesWorker @AssistedInject constructor(
    private val prefs: DataStoreRepository,
    private val prayerTimesRepository: PrayerTimesRepository,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val lat = prefs.get(DataStoreRepository.locationLat, 0.0).first()
        val lng = prefs.get(DataStoreRepository.locationLng, 0.0).first()

        if (lat == 0.0 && lng == 0.0) return Result.failure()

        val result = prayerTimesRepository.getPrayerTimesByCoordinates(lat, lng)
        return if (result is LoadingState.Success) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}