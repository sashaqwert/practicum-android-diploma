package ru.practicum.android.diploma.filter.domain.repository

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterIndustry

interface IndustryRepository {
    suspend fun getIndustry(): Resource<List<FilterIndustry>>
}
