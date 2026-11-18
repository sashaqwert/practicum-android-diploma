package ru.practicum.android.diploma.filter.di.domain

import org.koin.dsl.module
import ru.practicum.android.diploma.filter.domain.usecase.CountryInteractor
import ru.practicum.android.diploma.filter.domain.usecase.IndustryInteractor
import ru.practicum.android.diploma.filter.domain.usecase.RegionInteractor
import ru.practicum.android.diploma.filter.domain.usecaseimpl.CountryInteractorImpl
import ru.practicum.android.diploma.filter.domain.usecaseimpl.IndustryInteractorImpl
import ru.practicum.android.diploma.filter.domain.usecaseimpl.RegionInteractorImpl

val filterDomainModule = module {
    single<IndustryInteractor> {
        IndustryInteractorImpl(get())
    }
    single<CountryInteractor> {
        CountryInteractorImpl(get())
    }
    single<RegionInteractor> {
        RegionInteractorImpl(get())
    }
}
