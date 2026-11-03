package ru.practicum.android.diploma.Filter.data.response

import ru.practicum.android.diploma.common.data.network.response.Response
import ru.practicum.android.diploma.Filter.data.dto.FilterAreaDto

class AreaResponse(
    val result: ArrayList<FilterAreaDto>
) : Response()
