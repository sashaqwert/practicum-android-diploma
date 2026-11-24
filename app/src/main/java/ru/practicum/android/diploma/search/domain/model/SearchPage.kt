package ru.practicum.android.diploma.search.domain.model

import ru.practicum.android.diploma.common.domain.models.VacancyCard

data class SearchPage(
    val vacancies: List<VacancyCard>,
    val found: Int,
    val page: Int,
    val hasNextPage: Boolean
)
