package ru.practicum.android.diploma.common.data.network.dto.details

import ru.practicum.android.diploma.common.data.network.dto.areas.FilterAreaDto
import ru.practicum.android.diploma.common.data.network.dto.industries.FilterIndustryDto
import ru.practicum.android.diploma.common.data.network.dto.vacancies.Response
import ru.practicum.android.diploma.common.data.network.dto.vacancies.VacancyCardSalaryDto

data class VacancyDetailResponse(
    val address: AddressDto?,
    val area: FilterAreaDto,
    val contacts: ContactsDto?,
    val description: String,
    val employer: EmployerDto,
    val employment: EmploymentDto,
    val experience: ExperienceDto?,
    val id: String,
    val industry: FilterIndustryDto,
    val name: String,
    val salary: VacancyCardSalaryDto?,
    val schedule: ScheduleDto,
    val skills: List<String>,
    val url: String
) : Response()
