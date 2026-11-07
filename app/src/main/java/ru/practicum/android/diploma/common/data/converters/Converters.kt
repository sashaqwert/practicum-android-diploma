package ru.practicum.android.diploma.common.data.converters

import androidx.core.text.HtmlCompat
import ru.practicum.android.diploma.common.data.dto.FilterAreaDto
import ru.practicum.android.diploma.common.data.dto.FilterIndustryDto
import ru.practicum.android.diploma.common.data.dto.VacancyCardSalaryDto
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.common.domain.models.VacancyCardSalary
import ru.practicum.android.diploma.details.data.dto.AddressDto
import ru.practicum.android.diploma.details.data.dto.ContactsDto
import ru.practicum.android.diploma.details.data.dto.EmployerDto
import ru.practicum.android.diploma.details.data.dto.EmploymentDto
import ru.practicum.android.diploma.details.data.dto.ExperienceDto
import ru.practicum.android.diploma.details.data.dto.PhoneDto
import ru.practicum.android.diploma.details.data.dto.ScheduleDto
import ru.practicum.android.diploma.details.data.response.VacancyDetailResponse
import ru.practicum.android.diploma.details.domain.model.Address
import ru.practicum.android.diploma.details.domain.model.Contacts
import ru.practicum.android.diploma.details.domain.model.Employer
import ru.practicum.android.diploma.details.domain.model.Employment
import ru.practicum.android.diploma.details.domain.model.Experience
import ru.practicum.android.diploma.details.domain.model.Phone
import ru.practicum.android.diploma.details.domain.model.Schedule
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.search.data.response.VacancyResponse
import ru.practicum.android.diploma.search.domain.model.SearchResult

class Converters {
    private fun toAddress(addressDto: AddressDto?): Address? {
        return if (addressDto != null) {
            Address(
                building = addressDto.building,
                city = addressDto.city,
                id = addressDto.id,
                raw = addressDto.raw,
                street = addressDto.street
            )
        } else {
            null
        }
    }

    private fun toFilterArea(areaDto: FilterAreaDto): FilterArea {
        return FilterArea(
            id = areaDto.id,
            name = areaDto.name,
            parentId = areaDto.parentId,
            areas = toFilterAreas(areaDto.areas)
        )
    }

    private fun toFilterAreas(areas: List<FilterAreaDto>): List<FilterArea> {
        return areas.map { dto ->
            toFilterArea(dto)
        }
    }

    private fun toContacts(contactsDto: ContactsDto?): Contacts? {
        return if (contactsDto != null) {
            Contacts(
                email = contactsDto.email,
                id = contactsDto.id,
                name = contactsDto.name,
                phones = toPhones(contactsDto.phones)
            )
        } else {
            null
        }
    }

    private fun toPhones(phonesDto: List<PhoneDto>): List<Phone> {
        return phonesDto.map { dto ->
            Phone(
                comment = dto.comment,
                formatted = dto.formatted
            )
        }
    }

    private fun toEmployer(employerDto: EmployerDto): Employer {
        return Employer(
            id = employerDto.id,
            logo = employerDto.logo,
            name = employerDto.name
        )
    }

    private fun toEmployment(employmentDto: EmploymentDto?): Employment? {
        return if (employmentDto != null) {
            Employment(
                id = employmentDto.id,
                name = employmentDto.name
            )
        } else {
            null
        }
    }

    private fun toExperience(experienceDto: ExperienceDto?): Experience? {
        return if (experienceDto != null) {
            Experience(
                id = experienceDto.id,
                name = experienceDto.name
            )
        } else {
            null
        }
    }

    private fun toFilterIndustry(industryDto: FilterIndustryDto): FilterIndustry {
        return FilterIndustry(
            id = industryDto.id,
            name = industryDto.name
        )
    }

    private fun toSalary(salaryDto: VacancyCardSalaryDto?): VacancyCardSalary? {
        return if (salaryDto != null) {
            VacancyCardSalary(
                from = salaryDto.from,
                to = salaryDto.to,
                currency = salaryDto.currency
            )
        } else {
            null
        }
    }


    private fun toSchedule(scheduleDto: ScheduleDto?): Schedule? {
        return if (scheduleDto != null) {
            Schedule(
                id = scheduleDto.id,
                name = scheduleDto.name
            )
        } else {
            null
        }
    }

    fun toVacancyDetail(
        detailResponse: VacancyDetailResponse
    ): VacancyDetail {
        return VacancyDetail(
            address = toAddress(detailResponse.address),
            area = toFilterArea(detailResponse.area),
            contacts = toContacts(detailResponse.contacts),
            description = HtmlCompat.fromHtml(
                detailResponse.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).toString(),
            employer = toEmployer(detailResponse.employer),
            employment = toEmployment(detailResponse.employment),
            experience = toExperience(detailResponse.experience),
            id = detailResponse.id,
            industry = toFilterIndustry(detailResponse.industry),
            name = detailResponse.name,
            salary = toSalary(detailResponse.salary),
            schedule = toSchedule(detailResponse.schedule),
            skills = detailResponse.skills,
            url = detailResponse.url,
            isFavorite = false
        )
    }

    fun convertVacancies(vacancies: VacancyResponse): SearchResult {
        val vacanciesMapped = vacancies.vacancyCards.map {
            VacancyCard(
                id = it.id,
                name = it.name,
                company = it.company,
                city = it.city,
                salary = toSalary(it.salary),
                logo = it.logo
            )
        }

        return SearchResult(
            vacancies = vacanciesMapped,
            found = vacancies.found,
            page = vacancies.page,
            pages = vacancies.pages
        )
    }
}
