package ru.practicum.android.diploma.common.data.network.client

import ru.practicum.android.diploma.common.data.network.response.Response

interface NetworkClient {
    suspend fun doRequest(intent: RequestIntent, isConnected: Boolean): Response
}
