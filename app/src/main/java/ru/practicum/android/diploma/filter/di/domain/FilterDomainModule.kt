package ru.practicum.android.diploma.filter.di.domain

import org.koin.dsl.module
import ru.practicum.android.diploma.filter.domain.usecase.IndustryInteractor
import ru.practicum.android.diploma.filter.domain.usecaseimpl.IndustryInteractorImpl

val filterDomainModule = module {
    single<IndustryInteractor> {
        IndustryInteractorImpl(get())
    }
}
