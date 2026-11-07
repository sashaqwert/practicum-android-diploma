package ru.practicum.android.diploma.search.domain.repository

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.domain.model.SearchResult

interface SearchRepository {
    suspend fun searchVacancies(options: Map<String, String>): Resource<SearchResult>
}
