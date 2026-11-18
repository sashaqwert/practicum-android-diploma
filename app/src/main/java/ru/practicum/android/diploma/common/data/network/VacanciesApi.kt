package ru.practicum.android.diploma.common.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.common.data.dto.FilterAreaDto
import ru.practicum.android.diploma.details.data.response.VacancyDetailResponse
import ru.practicum.android.diploma.filter.data.response.AreaResponse
import ru.practicum.android.diploma.filter.data.response.IndustryResponse
import ru.practicum.android.diploma.search.data.response.VacancyResponse

interface VacanciesApi {
    @GET("/areas")
    suspend fun getAreas(): List<FilterAreaDto>

    @GET("/industries")
    suspend fun getIndustries(): IndustryResponse

    @GET("/vacancies")
    suspend fun getFilteredVacancies(@QueryMap options: Map<String, String>): VacancyResponse

    @GET("/vacancies/{id}")
    suspend fun getVacancyById(@Path("id") id: String): VacancyDetailResponse
}
