package ru.practicum.android.diploma.details.data.repositoryimpl

import android.util.Log
import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.data.response.VacancyDetailResponse
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.domain.repository.DetailsRepository
import ru.practicum.android.diploma.favorite.domain.repository.FavoriteRepository
import ru.practicum.android.diploma.util.NetworkConnectedCheck
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class DetailsRepositoryImpl(
    private val client: NetworkClient,
    private val connectionCheck: NetworkConnectedCheck,
    private val converter: Converters,
    private val favoriteRepository: FavoriteRepository
) : DetailsRepository {

    override suspend fun getDetailsInfo(vacancyId: String): Resource<VacancyDetail> {
        val isConnected = connectionCheck.isConnected()

        val response = try {
            client.doRequest(RequestIntent.GetVacancyById(vacancyId), isConnected)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Log.e("DetailsRepository", "Network error", e)
            null
        } catch (e: Throwable) {
            Log.e("DetailsRepository", "Unexpected error", e)
            null
        }

        if (response == null) {
            return Resource.Error(NO_INTERNET)
        }

        return when (response.code) {
            HTTP_OK -> {
                if (response is VacancyDetailResponse) {
                    val vacancyDetail = converter.toVacancyDetail(response)
                    val fromRoom = checkFromRoom(vacancyId)
                    when (fromRoom) {
                        is Resource.Error -> vacancyDetail.isFavorite = false
                        is Resource.Success -> vacancyDetail.isFavorite = true
                    }
                    Resource.Success(vacancyDetail)
                } else {
                    Resource.Error(NO_DETAIL_RESPONSE)
                }
            }

            NO_INTERNET -> {
                checkFromRoom(vacancyId)
            }

            NOT_FOUND -> {
                favoriteRepository.deleteVacancyBYId(vacancyId)
                Resource.Error(NOT_FOUND)
            }

            else -> Resource.Error(response.code)
        }
    }

    private suspend fun checkFromRoom(vacancyId: String): Resource<VacancyDetail> {
        val fromRoom = favoriteRepository.getVacancyById(vacancyId)
        return when (fromRoom) {
            is Resource.Success -> fromRoom
            else -> Resource.Error(NO_INTERNET)
        }
    }

    companion object {
        private const val HTTP_OK = 200
        private const val NO_INTERNET = -1
        private const val NO_DETAIL_RESPONSE = -2
        private const val NOT_FOUND = 404
    }
}
