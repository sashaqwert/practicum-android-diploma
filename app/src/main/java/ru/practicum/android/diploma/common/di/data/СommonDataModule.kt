package ru.practicum.android.diploma.common.di.data

import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.data.network.VacanciesApi

val commonDataModule = module {

    single {
        Converters(get())
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request =
                    chain.request()
                        .newBuilder()
                        .header("Authorization", "Bearer ${BuildConfig.API_ACCESS_TOKEN}")
                        .build()

                chain.proceed(request)
            }
            .build()
    }

    single<VacanciesApi> {
        Retrofit.Builder()
            .baseUrl("https://android-diploma.education-services.ru")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VacanciesApi::class.java)
    }

}
