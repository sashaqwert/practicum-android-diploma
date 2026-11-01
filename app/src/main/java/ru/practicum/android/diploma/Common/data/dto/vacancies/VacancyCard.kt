package ru.practicum.android.diploma.Common.data.dto.vacancies

data class VacancyCard(
    val city: String,
    val company: String,
    val id: String,
    val logo: String,
    val name: String,
    val salary: VacancyCardSalary
)
