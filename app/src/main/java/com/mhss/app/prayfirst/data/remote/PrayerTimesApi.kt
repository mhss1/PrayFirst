package com.mhss.app.prayfirst.data.remote

import com.mhss.app.prayfirst.domain.data.PrayerTimesApi
import com.mhss.app.prayfirst.domain.model.PrayerTimesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrayerTimesAladhanApi(
    private val client: HttpClient
): PrayerTimesApi {

    override suspend fun getPrayerTimesByAddress(address: String, year: String, month: String): PrayerTimesResponse {
        return withContext(Dispatchers.IO){
            client.get(NetworkConstants.BASE_URL){
                url {
                    appendPathSegments(
                        NetworkConstants.BY_ADDRESS_PATH,
                        year,
                        month
                    )
                }
                parameter("address", address)
                parameter("iso8601", true)
            }.body()
        }
    }

    override suspend fun getPrayerTimesByCoordinates(latitude: Double, longitude: Double, year: String, month: String): PrayerTimesResponse {
        return withContext(Dispatchers.IO) {
            client.get(NetworkConstants.BASE_URL) {
                url {
                    appendPathSegments(
                        NetworkConstants.BY_COORDINATES_PATH,
                        year,
                        month
                    )
                }
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("iso8601", true)
            }.body()
        }
    }

}