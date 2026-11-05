package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.domain.model.SearchResult
import ru.practicum.android.diploma.search.domain.repository.SearchRepository

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override suspend fun searchVacancies(query: String): Resource<SearchResult> = repository.searchVacancies(query)
}
