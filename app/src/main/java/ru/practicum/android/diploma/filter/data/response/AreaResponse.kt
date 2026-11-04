package ru.practicum.android.diploma.filter.data.response

import ru.practicum.android.diploma.common.data.dto.FilterAreaDto
import ru.practicum.android.diploma.common.data.network.client.Response

class AreaResponse(
    val result: ArrayList<FilterAreaDto>
) : Response()
