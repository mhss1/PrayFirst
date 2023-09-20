package com.mhss.app.prayfirst.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhss.app.prayfirst.domain.location.LocationManager
import com.mhss.app.prayfirst.domain.model.PrayerTime
import com.mhss.app.prayfirst.domain.repository.PrayerTimesRepository
import com.mhss.app.prayfirst.util.formatTimerTime
import com.mhss.app.prayfirst.util.now
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val repository: PrayerTimesRepository
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.None)
    val loadingState: StateFlow<LoadingState> = _loadingState

    val location = repository.getSavedLocationTitle().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(3000), ""
    )

    val latestPrayerTimes = repository.getLatestPrayerTimes().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(3000), emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val nextPrayerData = latestPrayerTimes.filterNot { it.isEmpty() }.flatMapLatest { prayerTimes ->
        flow {
            while(true) {
                val nextPrayer = prayerTimes.firstOrNull { prayerTime ->
                    prayerTime.time > now()
                } ?: return@flow

                val prevPrayerTime = prayerTimes.indexOfFirst {
                    it.time > now()
                }.let {
                    when {
                        it <= 0 -> repository.getLatestIsha(nextPrayer.time)?.time ?: return@flow
                        else -> prayerTimes[it - 1].time
                    }
                }

                val endTimeMillis = nextPrayer.time
                val totalTime = endTimeMillis - prevPrayerTime

                while (now() < endTimeMillis) {
                    val remainingMillis = endTimeMillis - now()
                    emit(
                        NextPrayerData(
                            nextPrayer,
                            remainingMillis.toFloat() / totalTime,
                            remainingMillis.formatTimerTime()
                        )
                    )
                    delay(1000)
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), null)

    fun getPrayerTimesForAddress(address: String) = viewModelScope.launch {
        _loadingState.update { LoadingState.Loading }
        val response = repository.getPrayerTimesByAddress(address)
        _loadingState.update { response }
    }

    fun getPrayerTimesForDetectedLocation() = viewModelScope.launch {
        _loadingState.update { LoadingState.Loading }
        val lastLocation = locationManager.getLastLocation()
        val response =
            lastLocation?.let {
                repository.getPrayerTimesByCoordinates(
                    it.latitude,
                    it.longitude
                )
            } ?: LoadingState.Error.LocationDetectionFailed
        _loadingState.update { response }
    }


    data class NextPrayerData(
        val prayer: PrayerTime,
        val progress: Float,
        val remainingTimeString: String
    )
}