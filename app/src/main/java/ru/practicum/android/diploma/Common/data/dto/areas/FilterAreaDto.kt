package ru.practicum.android.diploma.Common.data.dto.areas

data class FilterAreaDto(
    val id: String,
    val name: String,
    val parentId: Int?,
    val areas: List<FilterAreaDto>
)
