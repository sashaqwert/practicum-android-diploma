package ru.practicum.android.diploma.search.di.data

import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.common.data.network.VacanciesApi
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RetrofitNetworkClient
import ru.practicum.android.diploma.search.data.repository_impl.SearchRepositoryImpl
import ru.practicum.android.diploma.search.domain.repository.SearchRepository
import ru.practicum.android.diploma.util.NetworkConnectedCheck

val searchDataModule = module {
    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    factory {
        Gson()
    }

    factory {
        NetworkConnectedCheck(androidContext())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }
}
