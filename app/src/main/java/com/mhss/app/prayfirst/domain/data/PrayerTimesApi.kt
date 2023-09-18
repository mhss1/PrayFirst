package com.mhss.app.prayfirst.domain.data

import com.mhss.app.prayfirst.domain.model.PrayerTimesResponse

interface PrayerTimesApi {

    suspend fun getPrayerTimesByAddress(address: String, year: String, month: String): PrayerTimesResponse

    suspend fun getPrayerTimesByCoordinates(latitude: Double, longitude: Double, year: String, month: String): PrayerTimesResponse
}