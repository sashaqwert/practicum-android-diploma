package ru.practicum.android.diploma.common.data.dto.vacancies

data class VacancyResponse(
    val found: Int,
    val vacancyCards: List<VacancyCardDto>,
    val page: Int,
    val pages: Int
) : Response()
