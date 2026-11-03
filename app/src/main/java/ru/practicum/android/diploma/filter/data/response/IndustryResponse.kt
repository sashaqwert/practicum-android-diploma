package ru.practicum.android.diploma.filter.data.response

import ru.practicum.android.diploma.common.data.network.client.Response
import ru.practicum.android.diploma.filter.data.dto.FilterIndustryDto

class IndustryResponse(
    val result: ArrayList<FilterIndustryDto>
) : Response()
