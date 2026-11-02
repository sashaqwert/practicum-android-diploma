package ru.practicum.android.diploma.common.data.client

import ru.practicum.android.diploma.common.data.dto.vacancies.Response

interface NetworkClient {
    suspend fun doRequest(intent: RequestIntent, isConnected: Boolean): Response
}
