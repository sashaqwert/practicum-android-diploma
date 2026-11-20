package ru.practicum.android.diploma.filter.domain.repository

import ru.practicum.android.diploma.filter.domain.model.FilterSettings

interface FilterSettingsRepository {
    fun read(): FilterSettings
    fun save(state: FilterSettings)
}
