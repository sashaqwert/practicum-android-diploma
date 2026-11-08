package ru.practicum.android.diploma.favorite.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.details.domain.model.VacancyDetail

interface FavoriteRepository {
    fun getAllVacancies(): Flow<List<VacancyDetail>>
    suspend fun insertVacancy(vacancyDetail: VacancyDetail)
    suspend fun deleteVacancy(vacancyDetail: VacancyDetail)
}
