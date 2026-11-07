package ru.practicum.android.diploma.details.di.domain

import org.koin.dsl.module
import ru.practicum.android.diploma.details.domain.usecase.DetailsInteractor
import ru.practicum.android.diploma.details.domain.usecaseimpl.DetailsInteractorImpl

val detailsDomainModule = module {
    factory<DetailsInteractor> { DetailsInteractorImpl(get()) }
}
