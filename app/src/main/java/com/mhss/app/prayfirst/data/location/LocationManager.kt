package com.mhss.app.prayfirst.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mhss.app.prayfirst.domain.location.LocationManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidLocationManager(context: Context): LocationManager {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getLastLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationClient.lastLocation.addOnSuccessListener {
            continuation.resume(it)
        }.addOnFailureListener {
            continuation.resume(null)
        }
    }

}