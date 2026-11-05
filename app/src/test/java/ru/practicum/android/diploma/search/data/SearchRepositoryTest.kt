package ru.practicum.android.diploma.search.data

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.practicum.android.diploma.common.data.dto.VacancyCardSalaryDto
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.data.network.client.Response
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.data.dto.VacancyCardDto
import ru.practicum.android.diploma.search.data.repository_impl.SearchRepositoryImpl
import ru.practicum.android.diploma.search.data.response.VacancyResponse
import ru.practicum.android.diploma.search.domain.model.SearchResult
import java.io.IOException

class SearchRepositoryTest {
    @Test
    fun preservesTotalFoundAndMapsCards() = runBlocking {
        val client = TestNetworkClient {
            VacancyResponse(
                found = 42,
                vacancyCards = listOf(VacancyCardDto(
                    city = "Moscow",
                    company = null,
                    id = "123",
                    logo = null,
                    name = "Android developer",
                    salary = VacancyCardSalaryDto(currency = "RUR", from = 100000, to = null)
                )),
                page = 0,
                pages = 3
            ).apply { code = 200 }
        }
        val repository = SearchRepositoryImpl(client) { true }

        val result = repository.searchVacancies("android")
        assertTrue(result is Resource.Success<*>)
        val data = (result as Resource.Success<*>).data
        assertTrue(data is SearchResult)
        data as SearchResult
        assertEquals(42, data.found)
        assertEquals("123", data.vacancies.single().id)
        assertEquals(null, data.vacancies.single().company)
        assertEquals(100000, data.vacancies.single().salary?.from)
        assertEquals(null, data.vacancies.single().salary?.to)
        assertEquals(mapOf("text" to "android"), client.options)
    }

    @Test
    fun preservesNoInternetCode() = runBlocking {
        val repository = SearchRepositoryImpl(TestNetworkClient { Response().apply { code = -1 } }) { false }

        assertEquals(Resource.Error(-1), repository.searchVacancies("android"))
    }

    @Test
    fun preservesServerCode() = runBlocking {
        val repository = SearchRepositoryImpl(TestNetworkClient { Response().apply { code = 503 } }) { true }

        assertEquals(Resource.Error(503), repository.searchVacancies("android"))
    }

    @Test
    fun ioFailureBecomesServerErrorWhenConnectionRemainsAvailable() = runBlocking {
        val repository = SearchRepositoryImpl(TestNetworkClient { throw IOException("Timeout") }) { true }

        assertEquals(Resource.Error(500), repository.searchVacancies("android"))
    }

    @Test
    fun ioFailureReportsConnectionLostDuringRequest() = runBlocking {
        var checks = 0
        val repository = SearchRepositoryImpl(TestNetworkClient { throw IOException("Disconnected") }) {
            checks++ == 0
        }

        assertEquals(Resource.Error(-1), repository.searchVacancies("android"))
    }

    @Test
    fun readsDocumentedItemsField() {
        val json = """
            {"found":42,"page":0,"pages":3,"items":[
              {"id":"123","name":"Android developer","company":null,"city":null,"salary":null,"logo":null}
            ]}
        """.trimIndent()

        val response = Gson().fromJson(json, VacancyResponse::class.java)

        assertEquals(1, response.vacancyCards.size)
        assertEquals(42, response.found)
        assertEquals(null, response.vacancyCards.single().city)
    }

    private class TestNetworkClient(private val response: () -> Response) : NetworkClient {
        var options: Map<String, String>? = null

        override suspend fun doRequest(intent: RequestIntent, isConnected: Boolean): Response {
            options = (intent as RequestIntent.GetFilteredVacancies).options
            return response()
        }
    }
}
