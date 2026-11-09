package ru.practicum.android.diploma.favorite.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.details.domain.model.VacancyDetail

interface FavoriteInteractor {
    fun getAllVacancies(): Flow<List<VacancyDetail>>
    suspend fun insertVacancy(vacancyDetail: VacancyDetail)
    suspend fun deleteVacancy(vacancyDetail: VacancyDetail)
}
