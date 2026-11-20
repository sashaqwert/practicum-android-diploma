package ru.practicum.android.diploma.filter.domain.repository

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterArea

interface LocationRepository {
    suspend fun getLocation(): Resource<List<FilterArea>>
    suspend fun getRegions(countryId: Int?): Resource<List<FilterArea>>
}
