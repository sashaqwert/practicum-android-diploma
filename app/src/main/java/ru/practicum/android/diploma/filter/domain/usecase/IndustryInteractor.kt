package ru.practicum.android.diploma.filter.domain.usecase

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterIndustry

interface IndustryInteractor {
    suspend fun getIndustries(): Resource<List<FilterIndustry>>
}
