package com.mhss.app.prayfirst.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mhss.app.prayfirst.domain.model.PrayerTimeType
import com.mhss.app.prayfirst.domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.dataStore by preferencesDataStore(name = "preferences")

class DataStoreRepository(
    private val context: Context
) : PreferencesRepository {

    override suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { settings ->
                if (settings[key] != value)
                    settings[key] = value
            }
        }
    }

    override fun <T> get(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data.map { preferences -> preferences[key] ?: defaultValue }
    }

    companion object {
        val overlayMinutes = intPreferencesKey("overlay_minutes")
        val locationTitle = stringPreferencesKey("location_title")
        val locationLat = doublePreferencesKey("location_lat")
        val locationLng = doublePreferencesKey("location_long")

        val fajrLockScreen = booleanPreferencesKey("fajr_lock_screen")
        val zuhrLockScreen = booleanPreferencesKey("zuhr_lock_screen")
        val asrLockScreen = booleanPreferencesKey("asr_lock_screen")
        val maghribLockScreen = booleanPreferencesKey("maghrib_lock_screen")
        val ishaLockScreen = booleanPreferencesKey("isha_lock_screen")

        fun lockKeyToType(key: Preferences.Key<Boolean>): Int {
            return when (key) {
                fajrLockScreen -> PrayerTimeType.FAJR.ordinal
                zuhrLockScreen -> PrayerTimeType.ZUHR.ordinal
                asrLockScreen -> PrayerTimeType.ASR.ordinal
                maghribLockScreen -> PrayerTimeType.MAGHRIB.ordinal
                ishaLockScreen -> PrayerTimeType.ISHA.ordinal
                else -> PrayerTimeType.FAJR.ordinal
            }
        }
    }

}