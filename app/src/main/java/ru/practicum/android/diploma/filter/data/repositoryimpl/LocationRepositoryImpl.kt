package ru.practicum.android.diploma.filter.data.repositoryimpl

import android.util.Log
import okio.IOException
import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.filter.data.response.AreaResponse
import ru.practicum.android.diploma.filter.domain.repository.LocationRepository
import ru.practicum.android.diploma.util.NetworkConnectedCheck
import kotlin.coroutines.cancellation.CancellationException

class LocationRepositoryImpl(
    private val networkClient: NetworkClient,
    private val connectionCheck: NetworkConnectedCheck,
    private val converter: Converters
) : LocationRepository {
    override suspend fun getLocation(): Resource<List<FilterArea>> {
        val isConnected = connectionCheck.isConnected()

        val response = runCatching {
            networkClient.doRequest(RequestIntent.GetAreas, isConnected)
        }.getOrElse { error ->
            if (error is CancellationException) {
                throw error
            }
            val message = if (error is IOException) "Network error" else "Unexpected error"
            Log.e("LocationRepository", message, error)
            null
        }

        if (response == null) {
            return Resource.Error(NO_INTERNET)
        } else {
            return when (response.code) {
                HTTP_OK -> {
                    if (response is AreaResponse) {
                        val countries = converter.toFilterAreas(response.result)
                        Resource.Success(countries)
                    } else {
                        Resource.Error(NO_AREA_RESPONSE)
                    }
                }

                NO_INTERNET -> {
                    Resource.Error(NO_INTERNET)
                }

                SERVER_ERROR -> {
                    Resource.Error(SERVER_ERROR)
                }

                else -> {
                    Resource.Error(UNKNOWN_ERROR)
                }
            }
        }
    }

    companion object {
        private const val HTTP_OK = 200
        private const val NO_INTERNET = -1
        private const val NO_AREA_RESPONSE = -2
        private const val SERVER_ERROR = 500
        private const val UNKNOWN_ERROR = -3
    }
}
