package ru.practicum.android.diploma.filter.data.dto

data class FilterAreaDto(
    val id: String,
    val name: String,
    val parentId: Int?,
    val areas: List<FilterAreaDto>
)
