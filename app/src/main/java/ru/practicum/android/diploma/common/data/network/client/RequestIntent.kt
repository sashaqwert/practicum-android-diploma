package ru.practicum.android.diploma.common.data.network.client

import ru.practicum.android.diploma.common.data.dto.FilterRequest

sealed interface RequestIntent {
    data class GetFilterOptions(val request: FilterRequest) : RequestIntent
    data class GetFilteredVacancies(val options: Map<String, String>) : RequestIntent
    data class GetVacancyById(val id: String) : RequestIntent
}
