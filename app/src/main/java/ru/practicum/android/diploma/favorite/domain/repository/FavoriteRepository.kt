package ru.practicum.android.diploma.favorite.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.domain.model.VacancyDetail

interface FavoriteRepository {
    fun getAllVacancies(): Flow<Resource<List<VacancyDetail>>>
    suspend fun insertVacancy(vacancyDetail: VacancyDetail)
    suspend fun deleteVacancy(vacancyDetail: VacancyDetail)
    suspend fun getVacancyById(id: String): Resource<VacancyDetail>
    suspend fun deleteVacancyBYId(id: String): Int
}
