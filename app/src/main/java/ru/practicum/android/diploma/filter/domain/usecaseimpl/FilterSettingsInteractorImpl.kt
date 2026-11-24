package ru.practicum.android.diploma.filter.domain.usecaseimpl

import ru.practicum.android.diploma.filter.domain.model.FilterSettings
import ru.practicum.android.diploma.filter.domain.repository.FilterSettingsRepository
import ru.practicum.android.diploma.filter.domain.usecase.FilterSettingsInteractor

class FilterSettingsInteractorImpl(
    private val repository: FilterSettingsRepository
) : FilterSettingsInteractor {
    override fun read(): FilterSettings = repository.read()

    override fun save(state: FilterSettings) = repository.save(state)
}
