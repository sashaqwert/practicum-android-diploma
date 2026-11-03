package ru.practicum.android.diploma.Search.data.response

import ru.practicum.android.diploma.common.data.network.response.Response
import ru.practicum.android.diploma.Search.data.dto.VacancyCardDto

data class VacancyResponse(
    val found: Int,
    val page: Int,
    val pages: Int,
    val vacancyCards: List<VacancyCardDto>
) : Response()
