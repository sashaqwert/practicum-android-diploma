package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.domain.model.SearchResult

interface SearchInteractor {
    suspend fun searchVacancies(options: Map<String, String>): Resource<SearchResult>
}
