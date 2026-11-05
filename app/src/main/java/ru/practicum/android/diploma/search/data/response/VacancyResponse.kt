package ru.practicum.android.diploma.search.data.response

import ru.practicum.android.diploma.common.data.network.client.Response
import ru.practicum.android.diploma.search.data.dto.VacancyCardDto

data class VacancyResponse(
    val found: Int,
    val vacancyCards: List<VacancyCardDto>,
    val page: Int,
    val pages: Int
) : Response()
