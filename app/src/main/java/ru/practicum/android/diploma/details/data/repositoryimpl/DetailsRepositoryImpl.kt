package ru.practicum.android.diploma.details.data.repositoryimpl

import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.data.response.VacancyDetailResponse
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.domain.repository.DetailsRepository
import ru.practicum.android.diploma.util.NetworkConnectedCheck

class DetailsRepositoryImpl(
    private val client: NetworkClient,
    private val connectionCheck: NetworkConnectedCheck,
    private val converter: Converters
) : DetailsRepository {

    override suspend fun getDetailsInfo(vacancyId: String): Resource<VacancyDetail> {
        val isConnected = connectionCheck.isConnected()

        val response = client.doRequest(RequestIntent.GetVacancyById(vacancyId), isConnected)

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
            DELETED -> {
                Resource.Error(DELETED)
            }

            else -> Resource.Error(response.code)
        }
    }

    companion object {
        private const val HTTP_OK = 200
        private const val NO_INTERNET = -1
        private const val NO_DETAIL_RESPONSE = -2
        private const val DELETED = 404
    }
}

