package ru.practicum.android.diploma.Common.data.dto.details

import ru.practicum.android.diploma.Common.data.dto.areas.FilterAreaDto
import ru.practicum.android.diploma.Common.data.dto.vacancies.Response
import ru.practicum.android.diploma.Common.data.dto.industries.FilterIndustryDto
import ru.practicum.android.diploma.Common.data.dto.vacancies.VacancyCardSalary

data class VacancyDetailResponse(
    val address: Address?,
    val area: FilterAreaDto,
    val contacts: Contacts?,
    val description: String,
    val employer: Employer,
    val employment: Employment,
    val experience: Experience?,
    val id: String,
    val industry: FilterIndustryDto,
    val name: String,
    val salary: VacancyCardSalary?,
    val schedule: Schedule,
    val skills: List<String>,
    val url: String
) : Response()
