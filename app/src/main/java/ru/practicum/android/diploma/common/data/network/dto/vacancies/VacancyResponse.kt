package ru.practicum.android.diploma.common.data.network.dto.vacancies

data class VacancyResponse(
    val found: Int,
    val vacancyCards: List<VacancyCardDto>,
    val page: Int,
    val pages: Int
) : Response()
