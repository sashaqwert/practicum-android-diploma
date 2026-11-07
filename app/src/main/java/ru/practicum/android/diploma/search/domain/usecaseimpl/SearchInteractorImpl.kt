package ru.practicum.android.diploma.search.domain.usecaseimpl

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.domain.model.SearchResult
import ru.practicum.android.diploma.search.domain.repository.SearchRepository
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {
    override suspend fun searchVacancies(options: Map<String, String>): Resource<SearchResult> {
        return searchRepository.searchVacancies(options)
    }
}
