package ru.practicum.android.diploma.Filter.data.network

import retrofit2.http.GET
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.Common.data.dto.vacancies.VacancyResponse
import ru.practicum.android.diploma.Filter.data.dto.areas.AreaResponse
import ru.practicum.android.diploma.Filter.data.dto.industries.IndustryResponse

interface FilterVacanciesApi {
    @GET("/areas")
    suspend fun getAreas(): AreaResponse

    @GET("/industries")
    suspend fun getIndustries(): IndustryResponse

    @GET("/vacancies")
    suspend fun getFilteredVacancies(@QueryMap options: Map<String, String>): VacancyResponse
}
