package com.mhss.app.prayfirst.di

import android.content.Context
import androidx.room.Room
import com.mhss.app.prayfirst.data.alarm.PrayersAlarmsManager
import com.mhss.app.prayfirst.data.repository.DataStoreRepository
import com.mhss.app.prayfirst.data.location.AndroidLocationManager
import com.mhss.app.prayfirst.data.repository.PrayerTimesRepositoryImpl
import com.mhss.app.prayfirst.data.remote.PrayerTimesAladhanApi
import com.mhss.app.prayfirst.data.repository.PrayerAlarmRepositoryImpl
import com.mhss.app.prayfirst.data.room.AlarmsDao
import com.mhss.app.prayfirst.data.room.PrayerTimesDao
import com.mhss.app.prayfirst.data.room.PrayerTimesDatabase
import com.mhss.app.prayfirst.domain.alarm.AlarmsManager
import com.mhss.app.prayfirst.domain.data.PrayerTimesApi
import com.mhss.app.prayfirst.domain.location.LocationManager
import com.mhss.app.prayfirst.domain.repository.PreferencesRepository
import com.mhss.app.prayfirst.domain.repository.PrayerAlarmRepository
import com.mhss.app.prayfirst.domain.repository.PrayerTimesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository = DataStoreRepository(context)

    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context
    ): LocationManager = AndroidLocationManager(context)

    @Provides
    @Singleton
    fun providePrayerTimesRepository(
        api: PrayerTimesApi,
        dao: PrayerTimesDao,
        prefs: PreferencesRepository,
        alarms: PrayerAlarmRepository
    ): PrayerTimesRepository
    = PrayerTimesRepositoryImpl(
        api,
        dao,
        prefs,
        alarms
    )

    @Provides
    @Singleton
    fun providePrayerAlarmRepository(
        alarmsManager: AlarmsManager,
        alarmsDao: AlarmsDao,
        prayersDao: PrayerTimesDao
    ): PrayerAlarmRepository = PrayerAlarmRepositoryImpl(
        alarmsManager,
        alarmsDao,
        prayersDao
    )


    @Provides
    @Singleton
    fun providePrayerTimesDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        PrayerTimesDatabase::class.java,
        PrayerTimesDatabase.DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun providePrayerTimesDao(
        database: PrayerTimesDatabase
    ) = database.prayerTimesDao()

    @Provides
    @Singleton
    fun provideAlarmsDao(
        database: PrayerTimesDatabase
    ) = database.alarmsDao()

    @Provides
    @Singleton
    fun provideHttpClient() = HttpClient(Android) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    @Provides
    @Singleton
    fun providePrayerTimesApi(
        client: HttpClient
    ): PrayerTimesApi = PrayerTimesAladhanApi(client)

    @Provides
    @Singleton
    fun provideAlarmsManager(
        @ApplicationContext context: Context
    ): AlarmsManager = PrayersAlarmsManager(context)
}