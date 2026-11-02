package ru.practicum.android.diploma.common.data.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.common.data.dto.vacancies.Response
import ru.practicum.android.diploma.common.data.network.VacanciesApi

class RetrofitNetworkClient(private val vacanciesApi: VacanciesApi) : NetworkClient {
    override suspend fun doRequest(intent: RequestIntent, isConnected: Boolean): Response = withContext(Dispatchers.IO){
        if (!isConnected) return@withContext Response().apply { code = -1 }

        try {
            val response = when(intent){
                is RequestIntent.GetAreas -> vacanciesApi.getAreas()
                is RequestIntent.GetFilteredVacancies -> vacanciesApi.getFilteredVacancies(intent.options)
                is RequestIntent.GetIndustries -> vacanciesApi.getIndustries()
                is RequestIntent.GetVacancyById -> vacanciesApi.getVacancyById(intent.id)
            }

            response.apply { code = 200 }
        } catch (e: Exception){
            Response().apply { code = 500 }
        }
    }
}
