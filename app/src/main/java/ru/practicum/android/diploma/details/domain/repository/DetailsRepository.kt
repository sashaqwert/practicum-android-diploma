package ru.practicum.android.diploma.details.domain.repository

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.domain.model.VacancyDetail

interface DetailsRepository {
    suspend fun getDetailsInfo(vacancyId: String): Resource<VacancyDetail>
}
