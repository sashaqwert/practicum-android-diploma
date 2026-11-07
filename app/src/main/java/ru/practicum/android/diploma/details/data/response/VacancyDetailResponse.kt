package ru.practicum.android.diploma.details.data.response

import ru.practicum.android.diploma.common.data.dto.FilterAreaDto
import ru.practicum.android.diploma.common.data.dto.FilterIndustryDto
import ru.practicum.android.diploma.common.data.dto.VacancyCardSalaryDto
import ru.practicum.android.diploma.common.data.network.client.Response
import ru.practicum.android.diploma.details.data.dto.AddressDto
import ru.practicum.android.diploma.details.data.dto.ContactsDto
import ru.practicum.android.diploma.details.data.dto.EmployerDto
import ru.practicum.android.diploma.details.data.dto.EmploymentDto
import ru.practicum.android.diploma.details.data.dto.ExperienceDto
import ru.practicum.android.diploma.details.data.dto.ScheduleDto

data class VacancyDetailResponse(
    val address: AddressDto?,
    val area: FilterAreaDto,
    val contacts: ContactsDto?,
    val description: String,
    val employer: EmployerDto,
    val employment: EmploymentDto?,
    val experience: ExperienceDto?,
    val id: String,
    val industry: FilterIndustryDto,
    val name: String,
    val salary: VacancyCardSalaryDto?,
    val schedule: ScheduleDto?,
    val skills: List<String>,
    val url: String
) : Response()
