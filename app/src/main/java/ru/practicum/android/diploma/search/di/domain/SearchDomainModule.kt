package ru.practicum.android.diploma.search.di.domain

import org.koin.dsl.module
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractorImpl

val searchDomainModule = module {
    factory<SearchInteractor> { SearchInteractorImpl(get()) }
}
