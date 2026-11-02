package ru.practicum.android.diploma.common.data.dto.vacancies

data class VacancyCardDto(
    val city: String,
    val company: String,
    val id: String,
    val logo: String,
    val name: String,
    val salary: VacancyCardSalaryDto?
)
