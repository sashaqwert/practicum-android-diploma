package ru.practicum.android.diploma.common.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.util.NetworkConnectedCheck

val appModule = module {
    single {
        NetworkConnectedCheck(androidContext())
    }
}
