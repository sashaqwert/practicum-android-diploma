package ru.practicum.android.diploma.common.domain.models

data class FilterArea(
    val id: String,
    val name: String,
    val parentId: Int?,
    val areas: List<FilterArea>
)
