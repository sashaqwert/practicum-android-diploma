package ru.practicum.android.diploma.search.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.data.network.client.Response
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.data.repository_impl.SearchRepositoryImpl
import ru.practicum.android.diploma.search.data.response.VacancyResponse
import ru.practicum.android.diploma.util.NetworkConnectedCheck

@RunWith(AndroidJUnit4::class)
class SearchRepositoryPaginationTest {
    @Test
    fun passesPageAndPreservesDocumentedResponseMetadata() = runBlocking {
        val items = (1..20).joinToString(",") { id ->
            """{"id":"$id","name":"Android developer","company":null,"city":null,"salary":null,"logo":null}"""
        }
        val response = response("""
            {"found":42,"page":1,"pages":3,"items":[$items]}
        """.trimIndent())
        assertNotNull(response.vacancyCards)
        val client = RecordingClient(response)

        val result = repository(client).searchVacancies("android kotlin", 1)
        val data = (result as Resource.Success).data

        assertEquals(mapOf("text" to "android kotlin", "page" to "1"), client.options)
        assertEquals(1, data.page)
        assertEquals(3, data.pages)
        assertEquals(42, data.found)
        assertEquals(20, data.vacancies.size)
        assertEquals("1", data.vacancies.first().id)
    }

    @Test
    fun emptyResponsePreservesZeroPages() = runBlocking {
        val client = RecordingClient(response("""{"found":0,"page":0,"pages":0,"items":[]}"""))

        val result = repository(client).searchVacancies("missing", 0)
        val data = (result as Resource.Success).data

        assertEquals(0, data.page)
        assertEquals(0, data.pages)
        assertEquals(0, data.found)
        assertEquals(emptyList<Any>(), data.vacancies)
        assertEquals(mapOf("text" to "missing", "page" to "0"), client.options)
    }

    private fun response(json: String): VacancyResponse =
        Gson().fromJson(json, VacancyResponse::class.java).apply { code = 200 }

    private fun repository(client: NetworkClient): SearchRepositoryImpl {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return SearchRepositoryImpl(client, NetworkConnectedCheck(context))
    }

    private class RecordingClient(private val response: Response) : NetworkClient {
        var options: Map<String, String>? = null

        override suspend fun doRequest(intent: RequestIntent, isConnected: Boolean): Response {
            options = (intent as RequestIntent.GetFilteredVacancies).options
            return response
        }
    }
}
