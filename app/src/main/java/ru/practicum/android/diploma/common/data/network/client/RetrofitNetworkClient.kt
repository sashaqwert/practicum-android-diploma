package ru.practicum.android.diploma.common.data.network.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.android.diploma.common.data.network.VacanciesApi
import ru.practicum.android.diploma.common.data.network.client.Response

class RetrofitNetworkClient(private val vacanciesApi: VacanciesApi) : NetworkClient {
    override suspend fun doRequest(
        intent: RequestIntent,
        isConnected: Boolean
    ): Response =
        withContext(Dispatchers.IO) {
            if (!isConnected) {
                Response().apply { code = NO_INTERNET }
            } else {
                try {
                    val response = when (intent) {
                        is RequestIntent.GetAreas -> vacanciesApi.getAreas()
                        is RequestIntent.GetFilteredVacancies -> vacanciesApi.getFilteredVacancies(intent.options)
                        is RequestIntent.GetIndustries -> vacanciesApi.getIndustries()
                        is RequestIntent.GetVacancyById -> vacanciesApi.getVacancyById(intent.id)
                    }

                    response.apply { code = HTTP_OK }
                } catch (e: HttpException) {
                    Response().apply { code = e.code() }
                }
            }
        }

    companion object {
        private const val HTTP_OK = 200
        private const val NO_INTERNET = -1
    }
}
