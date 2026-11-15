package ru.practicum.android.diploma.details.domain.usecaseimpl

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.domain.repository.DetailsRepository
import ru.practicum.android.diploma.details.domain.usecase.DetailsInteractor

class DetailsInteractorImpl(private val repository: DetailsRepository) : DetailsInteractor {
    override suspend fun getDetailsInfo(vacancyId: String): Resource<VacancyDetail> =
        repository.getDetailsInfo(vacancyId)
}
