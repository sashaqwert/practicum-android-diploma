package ru.practicum.android.diploma.common.data.network.client

interface NetworkClient {
    suspend fun doRequest(intent: RequestIntent, isConnected: Boolean): Response
}
