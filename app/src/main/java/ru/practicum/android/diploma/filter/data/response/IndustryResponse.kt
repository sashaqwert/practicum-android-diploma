package ru.practicum.android.diploma.filter.data.response

import ru.practicum.android.diploma.common.data.dto.FilterIndustryDto
import ru.practicum.android.diploma.common.data.network.client.Response

class IndustryResponse(
    val result: ArrayList<FilterIndustryDto>
) : Response()
