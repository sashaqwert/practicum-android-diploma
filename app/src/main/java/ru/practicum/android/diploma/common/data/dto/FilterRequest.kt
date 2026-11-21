package ru.practicum.android.diploma.common.data.dto

sealed interface FilterRequest {
    data object GetAreas : FilterRequest
    data object GetIndustries : FilterRequest
}
