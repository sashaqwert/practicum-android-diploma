package ru.practicum.android.diploma.filter.domain.usecase

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.filter.domain.repository.IndustryRepository

interface IndustryInteractor {
    suspend fun getIndustries(industryRepository: IndustryRepository): Resource<List<FilterIndustry>>
}
