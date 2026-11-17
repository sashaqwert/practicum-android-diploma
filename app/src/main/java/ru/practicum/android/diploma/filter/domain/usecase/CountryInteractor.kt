package ru.practicum.android.diploma.filter.domain.usecase

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterArea

interface CountryInteractor {
    suspend fun getCountries(): Resource<List<FilterArea>>
}
