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
import java.io.IOException

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val isConnected: () -> Boolean
) : SearchRepository {
    override suspend fun searchVacancies(query: String): Resource<SearchResult> {
        val connected = isConnected()
        val options = mapOf("text" to query)

        val response = try {
            networkClient.doRequest(RequestIntent.GetFilteredVacancies(options), connected)
        } catch (_: IOException) {
            return Resource.Error(if (isConnected()) SERVER_ERROR else NO_INTERNET)
        }

        return if (response.code == HTTP_OK && response is VacancyResponse) {
            Resource.Success(SearchResult(convertVacancies(response), response.found))
        } else {
            Resource.Error(if (response.code == HTTP_OK) SERVER_ERROR else response.code)
        }
    }

    private fun convertVacancies(vacancies: VacancyResponse): List<VacancyCard> {
        return vacancies.vacancyCards.map {
            VacancyCard(
                id = it.id,
                name = it.name,
                company = it.company,
                city = it.city,
                salary = convertSalary(it.salary),
                logo = it.logo
            )
        }
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
        private const val SERVER_ERROR = 500
    }
}
