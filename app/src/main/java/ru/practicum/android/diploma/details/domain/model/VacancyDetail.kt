package ru.practicum.android.diploma.details.domain.model

import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.common.domain.models.VacancyCardSalary

data class VacancyDetail(
    val address: Address?,
    val area: FilterArea,
    val contacts: Contacts?,
    val description: String,
    val employer: Employer,
    val employment: Employment?,
    val experience: Experience?,
    val id: String,
    val industry: FilterIndustry,
    val name: String,
    val salary: VacancyCardSalary?,
    val schedule: Schedule?,
    val skills: List<String>,
    val url: String,
    val isFavorite: Boolean = false
)
