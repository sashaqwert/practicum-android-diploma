package ru.practicum.android.diploma.search.data

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.common.data.network.VacanciesApi
import ru.practicum.android.diploma.common.data.network.client.ApiTokenInterceptor
import ru.practicum.android.diploma.common.data.network.client.RetrofitNetworkClient
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.data.repository_impl.SearchRepositoryImpl
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractorImpl
import java.util.concurrent.TimeUnit

class SearchNetworkIntegrationTest {
    @Test
    fun searchesThroughRealNetworkClientRepositoryAndInteractor() = runBlocking {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse().setBody("""
                {"found":42,"page":0,"pages":3,"items":[
                  {"id":"123","name":"Android developer","company":null,"city":null,"salary":null,"logo":null}
                ]}
            """.trimIndent()))
            server.enqueue(MockResponse().setResponseCode(503))
            val api = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .client(OkHttpClient.Builder().addInterceptor(ApiTokenInterceptor("test-token")).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VacanciesApi::class.java)
            val interactor = SearchInteractorImpl(SearchRepositoryImpl(RetrofitNetworkClient(api)) { true })

            val result = interactor.searchVacancies("android kotlin")

            assertTrue(result is Resource.Success)
            val data = (result as Resource.Success).data
            assertEquals(42, data.found)
            assertEquals("123", data.vacancies.single().id)
            val request = server.takeRequest(1, TimeUnit.SECONDS)
            assertEquals("/vacancies?text=android%20kotlin", request?.path)
            assertEquals("test-token", request?.getHeader("Authorization"))
            assertEquals(Resource.Error(503), interactor.searchVacancies("android"))
        } finally {
            server.shutdown()
        }
    }
}
