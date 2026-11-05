package ru.practicum.android.diploma.search.domain.repository

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.VacancyCard

interface SearchRepository {
    suspend fun searchVacancies(query: String): Resource<List<VacancyCard>>
}
