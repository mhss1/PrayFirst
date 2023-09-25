package com.mhss.app.prayfirst.data.repository

import com.mhss.app.prayfirst.App
import com.mhss.app.prayfirst.R
import com.mhss.app.prayfirst.data.room.PrayerTimesDao
import com.mhss.app.prayfirst.domain.data.PrayerTimesApi
import com.mhss.app.prayfirst.domain.model.MonthData
import com.mhss.app.prayfirst.domain.model.PrayerTime
import com.mhss.app.prayfirst.domain.model.PrayerTimeType
import com.mhss.app.prayfirst.domain.model.toPrayerTime
import com.mhss.app.prayfirst.domain.model.toPrayerTimeEntities
import com.mhss.app.prayfirst.domain.repository.PrayerAlarmRepository
import com.mhss.app.prayfirst.domain.repository.PreferencesRepository
import com.mhss.app.prayfirst.domain.repository.PrayerTimesRepository
import com.mhss.app.prayfirst.presentation.main.LoadingState
import com.mhss.app.prayfirst.util.getCurrentMonthWithYear
import com.mhss.app.prayfirst.util.getNextMonthWithYear
import com.mhss.app.prayfirst.util.isToday
import com.mhss.app.prayfirst.util.now
import com.mhss.app.prayfirst.util.toFormattedDate
import com.mhss.app.prayfirst.util.tomorrow
import com.mhss.app.prayfirst.util.yesterday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class PrayerTimesRepositoryImpl(
    private val api: PrayerTimesApi,
    private val dao: PrayerTimesDao,
    private val prefs: PreferencesRepository,
    private val alarms: PrayerAlarmRepository
) : PrayerTimesRepository {

    override suspend fun getPrayerTimesByAddress(address: String): LoadingState = coroutineScope {
        val data = try {
            val currentMonthData = async {
                val (month, year) = getCurrentMonthWithYear()
                api.getPrayerTimesByAddress(address, year, month)
            }
            val nextMonthData = async {
                val (month, year) = getNextMonthWithYear()
                api.getPrayerTimesByAddress(address, year, month)
            }
            currentMonthData.await().monthsData + nextMonthData.await().monthsData

        } catch (e: IOException) {
            return@coroutineScope LoadingState.Error.Internet
        } catch (e: Exception) {
            e.printStackTrace()
            return@coroutineScope LoadingState.Error.Unknown
        }

        return@coroutineScope handelPrayerTimesResponse(
            data,
            data.first().meta.latitude,
            data.first().meta.longitude,
            address
        )
    }

    override suspend fun getPrayerTimesByCoordinates(
        latitude: Double,
        longitude: Double
    ): LoadingState = coroutineScope {
        val data = try {
            val currentMonthData = async {
                val (month, year) = getCurrentMonthWithYear()
                api.getPrayerTimesByCoordinates(latitude, longitude, year, month)
            }
            val nextMonthData = async {
                val (month, year) = getNextMonthWithYear()
                api.getPrayerTimesByCoordinates(latitude, longitude, year, month)
            }
            currentMonthData.await().monthsData + nextMonthData.await().monthsData
        } catch (e: IOException) {
            return@coroutineScope LoadingState.Error.Internet
        } catch (e: Exception) {
            e.printStackTrace()
            return@coroutineScope LoadingState.Error.Unknown
        }

        return@coroutineScope handelPrayerTimesResponse(
            data,
            latitude,
            longitude,
            data.first().meta.timezone
        )
    }

    private suspend fun handelPrayerTimesResponse(
        data: List<MonthData>,
        lat: Double,
        lng: Double,
        locationTitle: String
    ) = coroutineScope {
        if (data.isNotEmpty()) {
            launch(Dispatchers.IO) {
                val allPrayerTimes = data.flatMap {
                    it.toPrayerTimeEntities()
                }
                dao.savePrayerTimes(allPrayerTimes)

                listOf(
                    DataStoreRepository.fajrLockScreen,
                    DataStoreRepository.zuhrLockScreen,
                    DataStoreRepository.asrLockScreen,
                    DataStoreRepository.maghribLockScreen,
                    DataStoreRepository.ishaLockScreen
                ).forEach {
                    launch {
                        val lock = prefs.get(it, true).first()
                        if (lock) {
                            alarms.scheduleNewAlarm(DataStoreRepository.lockKeyToType(it))
                        }
                    }
                }
            }
            launch {
                prefs.save(DataStoreRepository.locationTitle, locationTitle)
                prefs.save(DataStoreRepository.locationLat, lat)
                prefs.save(DataStoreRepository.locationLng, lng)
            }

        } else {
            return@coroutineScope LoadingState.Error.NoDataForLocation
        }
        return@coroutineScope LoadingState.Success
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLatestPrayerTimes(): Flow<List<PrayerTime>> {
        return dao.getPrayerTimesByDateStream(
            now().toFormattedDate()
        ).flatMapLatest { list ->
            if (list.isNotEmpty() && (list.maxByOrNull { it.time }?.time
                    ?: 0L) < now()
            ) {
                dao.getPrayerTimesByDateStream(tomorrow().toFormattedDate())
            } else flow { emit(list) }
        }.map { list ->
            list.sortedBy { it.time }.map { it.toPrayerTime() }
        }
    }

    override suspend fun getLatestIsha(fajrTime: Long) = withContext(Dispatchers.IO) {
        if (fajrTime.isToday()) {
            dao.getPrayerTimeByDateAndType(
                yesterday().toFormattedDate(),
                PrayerTimeType.ISHA.ordinal
            )?.toPrayerTime()
        } else {
            dao.getPrayerTimeByDateAndType(
                now().toFormattedDate(),
                PrayerTimeType.ISHA.ordinal
            )?.toPrayerTime()
        }
    }

    override fun getSavedLocationTitle(): Flow<String> {
        return prefs.get(DataStoreRepository.locationTitle, App.getString(R.string.add_location))
    }
}