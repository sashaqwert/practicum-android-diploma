package ru.practicum.android.diploma.search.di.domain

import org.koin.dsl.module
import ru.practicum.android.diploma.search.domain.usecaseimpl.SearchInteractorImpl
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor

val searchDomainModule = module {
    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }
}
