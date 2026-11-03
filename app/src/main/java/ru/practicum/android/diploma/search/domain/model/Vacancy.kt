package ru.practicum.android.diploma.search.domain.model

data class Vacancy(
    val id: String,
    val name: String,
    val company: String,
    val city: String?,
    val logo: String?,
    val salary: Salary?
)
