package com.mhss.app.prayfirst.domain.repository

import com.mhss.app.prayfirst.domain.model.PrayerTime
import com.mhss.app.prayfirst.domain.model.PrayerTimeEntity
import com.mhss.app.prayfirst.presentation.main.LoadingState
import kotlinx.coroutines.flow.Flow

interface PrayerTimesRepository {

    suspend fun getPrayerTimesByAddress(address: String): LoadingState

    suspend fun getPrayerTimesByCoordinates(latitude: Double, longitude: Double): LoadingState

    fun getTodayPrayerTimes(): Flow<List<PrayerTime>>

    suspend fun getTomorrowFirstPrayer(): PrayerTime?

    suspend fun getYesterdayLastPrayer(): PrayerTime?

    fun getSavedLocationTitle(): Flow<String>
}