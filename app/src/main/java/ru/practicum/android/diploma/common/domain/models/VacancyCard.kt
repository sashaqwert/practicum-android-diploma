package ru.practicum.android.diploma.common.domain.models

data class VacancyCard(
    val id: String,
    val name: String,
    val company: String?,
    val city: String?,
    val salary: VacancyCardSalary?,
    val logo: String?
)
