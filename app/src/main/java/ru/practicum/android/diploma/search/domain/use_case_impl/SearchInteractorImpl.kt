package ru.practicum.android.diploma.search.domain.use_case_impl

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.domain.model.SearchResult
import ru.practicum.android.diploma.search.domain.repository.SearchRepository
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {
    override suspend fun searchVacancies(query: String): SearchResult {
        val response = searchRepository.searchVacancies(query) as Resource.Success
        return SearchResult(
            vacancies = response.data,
            found = response.data.size
        )
    }
}
