package ru.practicum.android.diploma.search.domain.model

data class SearchResult(
    val vacancies: List<Vacancy>,
    val found: Int
)
