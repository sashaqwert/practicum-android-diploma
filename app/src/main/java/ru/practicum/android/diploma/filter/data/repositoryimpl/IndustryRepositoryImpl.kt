package ru.practicum.android.diploma.filter.data.repositoryimpl

import android.util.Log
import okio.IOException
import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.filter.data.response.IndustryResponse
import ru.practicum.android.diploma.filter.domain.repository.IndustryRepository
import ru.practicum.android.diploma.util.NetworkConnectedCheck
import kotlin.coroutines.cancellation.CancellationException

class IndustryRepositoryImpl(
    private val networkClient: NetworkClient,
    private val connectionCheck: NetworkConnectedCheck,
    private val converter: Converters
) : IndustryRepository {
    override suspend fun getIndustry(): Resource<List<FilterIndustry>> {
        val isConnected = connectionCheck.isConnected()

        val response = runCatching {
            networkClient.doRequest(RequestIntent.GetIndustries, isConnected)
        }.getOrElse { error ->
            if (error is CancellationException) {
                throw error
            }
            val message = if (error is IOException) "Network error" else "Unexpected error"
            Log.e("IndustryRepository", message, error)
            null
        }

        if (response == null) {
            return Resource.Error(NO_INTERNET)
        }

        return when (response.code) {
            HTTP_OK -> {
                if (response is IndustryResponse) {
                    val industries = response.result.map { dto ->
                        converter.toFilterIndustry(dto)
                    }
                    Resource.Success(industries)
                } else {
                    Resource.Error(NO_INDUSTRY_RESPONSE)
                }
            }

            NO_INTERNET -> {
                Resource.Error(NO_INTERNET)
            }

            SERVER_ERROR -> {
                Resource.Error(SERVER_ERROR)
            }

            else -> {
                Resource.Error(response.code)
            }
        }
    }

    companion object {
        private const val HTTP_OK = 200
        private const val NO_INTERNET = -1
        private const val NO_INDUSTRY_RESPONSE = -2
        private const val SERVER_ERROR = 500
    }
}
