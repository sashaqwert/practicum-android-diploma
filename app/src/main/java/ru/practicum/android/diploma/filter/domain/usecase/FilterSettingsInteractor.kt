package ru.practicum.android.diploma.filter.domain.usecase

import ru.practicum.android.diploma.filter.domain.model.FilterSettings

interface FilterSettingsInteractor {
    fun read(): FilterSettings
    fun save(state: FilterSettings)
}
