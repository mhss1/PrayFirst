package com.mhss.app.prayfirst.domain.location

import android.location.Location

interface LocationManager {
    suspend fun getLastLocation(): Location?
}