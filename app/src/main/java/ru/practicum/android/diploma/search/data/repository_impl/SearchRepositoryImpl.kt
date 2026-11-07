package ru.practicum.android.diploma.search.data.repository_impl

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
    private val networkConnectedCheck: NetworkConnectedCheck
) : SearchRepository {
    override suspend fun searchVacancies(options: Map<String, String>): Resource<SearchResult> {
        val isConnected = networkConnectedCheck.isConnected()

        val response = networkClient.doRequest(RequestIntent.GetFilteredVacancies(options), isConnected)

        when (response.code) {
            HTTP_OK -> {
                if ((response as VacancyResponse).found > 0) {
                    return Resource.Success(
                        convertVacancies(response)
                    )
                } else {
                    return Resource.Success(
                        SearchResult(
                            vacancies = listOf(),
                            found = 0
                        )
                    )
                }
            }

            NO_INTERNET -> {
                return Resource.Error(NO_INTERNET)
            }

            else -> {
                return Resource.Error(response.code)
            }
        }
    }

    private fun convertVacancies(vacancies: VacancyResponse): SearchResult {
        val vacanciesMapped = vacancies.vacancyCards.map {
            VacancyCard(
                id = it.id,
                name = it.name,
                company = it.company,
                city = it.city,
                salary = convertSalary(it.salary),
                logo = it.logo
            )
        }

        return SearchResult(
            vacancies = vacanciesMapped,
            found = vacancies.found
        )
    }

    private fun convertSalary(salary: VacancyCardSalaryDto?): VacancyCardSalary? {
        salary ?: return null
        return VacancyCardSalary(
            from = salary.from,
            to = salary.to,
            currency = salary.currency
        )
    }

    companion object {
        private const val HTTP_OK = 200
        private const val NO_INTERNET = -1
    }
}
