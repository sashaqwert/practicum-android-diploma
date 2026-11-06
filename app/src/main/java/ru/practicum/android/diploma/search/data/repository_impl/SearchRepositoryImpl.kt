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
    override suspend fun searchVacancies(query: String, page: Int): Resource<SearchResult> {
        val isConnected = networkConnectedCheck.isConnected()
        val options = mapOf("text" to query, "page" to page.toString())

        val response = networkClient.doRequest(RequestIntent.GetFilteredVacancies(options), isConnected)

        return when (response.code) {
            HTTP_OK -> Resource.Success(convertVacancies(response as VacancyResponse))
            NO_INTERNET -> Resource.Error(NO_INTERNET)
            else -> Resource.Error(response.code)
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
            found = vacancies.found,
            page = vacancies.page,
            pages = vacancies.pages
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
