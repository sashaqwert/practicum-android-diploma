package ru.practicum.android.diploma.Details.data.response

import ru.practicum.android.diploma.Details.data.dto.AddressDto
import ru.practicum.android.diploma.Details.data.dto.ContactsDto
import ru.practicum.android.diploma.Details.data.dto.EmployerDto
import ru.practicum.android.diploma.Details.data.dto.EmploymentDto
import ru.practicum.android.diploma.Details.data.dto.ExperienceDto
import ru.practicum.android.diploma.common.data.network.response.Response
import ru.practicum.android.diploma.Details.data.dto.ScheduleDto
import ru.practicum.android.diploma.Filter.data.dto.FilterAreaDto
import ru.practicum.android.diploma.Filter.data.dto.FilterIndustryDto
import ru.practicum.android.diploma.Search.data.dto.VacancyCardSalaryDto

data class VacancyDetailResponse(
    val id: String,
    val name: String,
    val description: String,
    val salary: VacancyCardSalaryDto?,
    val address: AddressDto?,
    val experience: ExperienceDto?,
    val schedule: ScheduleDto?,
    val employment: EmploymentDto?,
    val contacts: ContactsDto?,
    val employer: EmployerDto,
    val area: FilterAreaDto,
    val skills: List<String>,
    val url: String,
    val industry: FilterIndustryDto,
) : Response()
