package ru.practicum.android.diploma.details.domain.usecase

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.domain.model.VacancyDetail

interface DetailsInteractor {
    suspend fun getDetailsInfo(vacancyId: String): Resource<VacancyDetail>
}
