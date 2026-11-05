package ru.practicum.android.diploma.search.data.dto

import ru.practicum.android.diploma.common.data.dto.VacancyCardSalaryDto

data class VacancyCardDto(
    val city: String?,
    val company: String?,
    val id: String,
    val logo: String?,
    val name: String,
    val salary: VacancyCardSalaryDto?
)
