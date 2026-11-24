package ru.practicum.android.diploma.search.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.domain.model.SearchPage

interface SearchInteractor {
    suspend fun searchVacancies(options: Map<String, String>, countryId: Int? = null): Resource<SearchPage>

    fun countCountryVacancies(): Flow<Int>
}
