package com.mhss.app.prayfirst.presentation.main

import android.location.Location
import com.mhss.app.prayfirst.domain.model.PrayerTimesResponse

sealed class LoadingState {
    data object None: LoadingState()
    data object Success: LoadingState()
    data object Loading: LoadingState()
    sealed class Error: LoadingState() {
        data object Internet: Error()
        data object NoDataForLocation: Error()
        data object LocationDetectionFailed: Error()
        data object Unknown: Error()
    }
}