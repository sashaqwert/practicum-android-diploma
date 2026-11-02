package ru.practicum.android.diploma.common.data.client

sealed interface RequestIntent {
    data object GetAreas : RequestIntent
    data object GetIndustries : RequestIntent
    data class GetFilteredVacancies(val options: Map<String, String>) : RequestIntent
    data class GetVacancyById(val id: String) : RequestIntent
}
