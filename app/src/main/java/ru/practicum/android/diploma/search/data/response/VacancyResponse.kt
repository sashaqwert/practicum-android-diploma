package ru.practicum.android.diploma.search.data.response

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.common.data.network.client.Response
import ru.practicum.android.diploma.search.data.dto.VacancyCardDto

data class VacancyResponse(
    val found: Int,
    @SerializedName("items") val vacancyCards: List<VacancyCardDto>,
    val page: Int,
    val pages: Int
) : Response()
