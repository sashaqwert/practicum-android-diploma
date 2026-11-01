package ru.practicum.android.diploma.Common.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.Common.data.dto.areas.AreaResponse
import ru.practicum.android.diploma.Common.data.dto.details.VacancyDetailResponse
import ru.practicum.android.diploma.Common.data.dto.industries.IndustryResponse
import ru.practicum.android.diploma.Common.data.dto.vacancies.VacancyResponse

interface VacanciesApi {
    @GET("/areas")
    suspend fun getAreas(): AreaResponse

    @GET("/industries")
    suspend fun getIndustries(): IndustryResponse

    @GET("/vacancies")
    suspend fun getFilteredVacancies(@QueryMap options: Map<String, String>): VacancyResponse

    @GET("/vacancies/{id}")
    suspend fun getVacancyById(@Path("id") id: String): VacancyDetailResponse
}
