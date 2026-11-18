package ru.practicum.android.diploma.common.domain.models

data class FilterArea(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val areas: List<FilterArea> = emptyList(),
    @Suppress("DataClassShouldBeImmutable") var parentName: String? = null
)
