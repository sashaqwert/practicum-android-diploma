package ru.practicum.android.diploma.filter.domain.usecaseimpl

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.filter.domain.repository.LocationRepository
import ru.practicum.android.diploma.filter.domain.usecase.RegionInteractor

class RegionInteractorImpl(
    private val repository: LocationRepository
) : RegionInteractor {
    override suspend fun getRegions(countryId: Int?): Resource<List<FilterArea>> {
        val allCountries = repository.getLocation()
        val result = when (allCountries) {
            is Resource.Error -> return Resource.Error(allCountries.code)
            is Resource.Success<List<FilterArea>> -> {
                val regions = allCountries.data.flatMap { country ->
                    country.areas.forEach { region ->
                        region.parentName = country.name
                    }
                    country.areas
                }

                if (countryId == null || countryId == 0) {
                    Resource.Success(regions)
                } else {
                    val countryRegions = regions.filter {
                        it.parentId == countryId
                    }
                    Resource.Success(countryRegions)
                }
            }
        }
        return result
    }
}
