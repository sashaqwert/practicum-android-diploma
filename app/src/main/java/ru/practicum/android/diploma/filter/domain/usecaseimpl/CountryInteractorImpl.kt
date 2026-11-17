package ru.practicum.android.diploma.filter.domain.usecaseimpl

import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.filter.domain.repository.LocationRepository
import ru.practicum.android.diploma.filter.domain.usecase.CountryInteractor

class CountryInteractorImpl(private val repository: LocationRepository) : CountryInteractor {
    override suspend fun getCountries(): Resource<List<FilterArea>> {
        return repository.getLocation()
    }
}
