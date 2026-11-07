package ru.practicum.android.diploma.search.data.repository_impl

import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.data.dto.VacancyCardSalaryDto
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.common.domain.models.VacancyCardSalary
import ru.practicum.android.diploma.search.data.response.VacancyResponse
import ru.practicum.android.diploma.search.domain.model.SearchResult
import ru.practicum.android.diploma.search.domain.repository.SearchRepository
import ru.practicum.android.diploma.util.NetworkConnectedCheck

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val networkConnectedCheck: NetworkConnectedCheck,
    private val converter: Converters
) : SearchRepository {
    override suspend fun searchVacancies(options: Map<String, String>): Resource<SearchResult> {
        val isConnected = networkConnectedCheck.isConnected()

        val response = networkClient.doRequest(RequestIntent.GetFilteredVacancies(options), isConnected)

        return when (response.code) {
            HTTP_OK -> {
                if ((response as VacancyResponse).found > 0) {
                    Resource.Success(
                        converter.convertVacancies(response)
                    )
                } else {
                    Resource.Success(
                        SearchResult(
                            vacancies = listOf(),
                            found = 0,
                            page = 0,
                            pages = 0
                        )
                    )
                }
            }

            NO_INTERNET -> {
                Resource.Error(NO_INTERNET)
            }

            else -> {
                Resource.Error(response.code)
            }
        }
    }

    companion object {
        private const val HTTP_OK = 200
        private const val NO_INTERNET = -1
    }
}
