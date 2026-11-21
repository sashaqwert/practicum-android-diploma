package ru.practicum.android.diploma.filter.domain.usecaseimpl

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.filter.domain.repository.LocationRepository
import ru.practicum.android.diploma.filter.domain.usecase.RegionInteractor

class RegionInteractorImpl(
    private val repository: LocationRepository
) : RegionInteractor {
    override suspend fun getRegions(countryId: Int?): Resource<List<FilterArea>> {
        return repository.getRegions(countryId)
    }
}
