package ru.practicum.android.diploma.common.data.network.client

import okhttp3.Interceptor
import okhttp3.Response

class ApiTokenInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().newBuilder().header("Authorization", token).build())
}
