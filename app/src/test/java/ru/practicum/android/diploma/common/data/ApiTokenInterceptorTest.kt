package ru.practicum.android.diploma.common.data

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.practicum.android.diploma.common.data.network.client.ApiTokenInterceptor
import java.util.concurrent.TimeUnit

class ApiTokenInterceptorTest {
    @Test
    fun sendsTokenInDocumentedAuthorizationHeader() {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse().setBody("{}"))
            val client = OkHttpClient.Builder().addInterceptor(ApiTokenInterceptor("test-token")).build()
            client.newCall(Request.Builder().url(server.url("/vacancies")).build()).execute().close()

            val request = server.takeRequest(1, TimeUnit.SECONDS)

            assertEquals("test-token", request?.getHeader("Authorization"))
        } finally {
            server.shutdown()
        }
    }
}
