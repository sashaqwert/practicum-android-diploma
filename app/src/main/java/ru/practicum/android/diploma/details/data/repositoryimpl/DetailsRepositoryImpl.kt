package ru.practicum.android.diploma.details.data.repositoryimpl

import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.data.response.VacancyDetailResponse
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.domain.repository.DetailsRepository
import ru.practicum.android.diploma.util.NetworkConnectedCheck
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class DetailsRepositoryImpl(
    private val client: NetworkClient,
    private val connectionCheck: NetworkConnectedCheck,
    private val converter: Converters
) : DetailsRepository {

    override suspend fun getDetailsInfo(vacancyId: String): Resource<VacancyDetail> {
        val isConnected = connectionCheck.isConnected()

        val response = try {
            client.doRequest(RequestIntent.GetVacancyById(vacancyId), isConnected)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            return Resource.Error(NO_INTERNET)
        } catch (e: Exception) {
            return Resource.Error(UNKNOWN_ERROR)
        }

        return when (response.code) {
            HTTP_OK -> {
                if (response is VacancyDetailResponse) {
                    val vacancyDetail = converter.toVacancyDetail(response)
                    Resource.Success(vacancyDetail)
                } else Resource.Error(NO_DETAIL_RESPONSE)
            }

            NO_INTERNET -> {
                Resource.Error(NO_INTERNET)
            }

            NOT_FOUND -> {
                Resource.Error(NOT_FOUND)
            }

            else -> Resource.Error(response.code)
        }
    }

    companion object {
        private const val HTTP_OK = 200
        private const val NO_INTERNET = -1
        private const val NO_DETAIL_RESPONSE = -2
        private const val NOT_FOUND = 404
        private const val UNKNOWN_ERROR = -3
    }
}

