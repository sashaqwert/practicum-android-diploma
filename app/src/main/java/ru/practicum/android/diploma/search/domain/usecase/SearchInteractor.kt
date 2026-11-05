package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.search.domain.model.SearchResult

interface SearchInteractor {
    suspend fun searchVacancies(query: String): SearchResult
}
