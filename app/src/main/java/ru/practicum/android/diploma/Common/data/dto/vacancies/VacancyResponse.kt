package ru.practicum.android.diploma.Common.data.dto.vacancies

data class VacancyResponse(
    val found: Int,
    val vacancyCards: List<VacancyCard>,
    val page: Int,
    val pages: Int
) : Response()
