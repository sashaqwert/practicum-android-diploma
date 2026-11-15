package ru.practicum.android.diploma.filter.domain.usecaseimpl

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.filter.domain.repository.IndustryRepository
import ru.practicum.android.diploma.filter.domain.usecase.IndustryInteractor

class IndustryInteractorImpl(
    private val repository: IndustryRepository
) : IndustryInteractor {
    override suspend fun getIndustries(): Resource<List<FilterIndustry>> {
        return repository.getIndustry()
    }
}
