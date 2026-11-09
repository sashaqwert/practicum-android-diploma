package ru.practicum.android.diploma.favorite.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.details.domain.model.VacancyDetail

interface FavoriteInteractor {
    fun getAllVacancies(details: List<VacancyDetail>): Flow<List<VacancyCard>>
    suspend fun insertVacancy(vacancyDetail: VacancyDetail)
    suspend fun deleteVacancy(vacancyDetail: VacancyDetail)
    suspend fun getVacancyById(id: String): VacancyDetail
}
