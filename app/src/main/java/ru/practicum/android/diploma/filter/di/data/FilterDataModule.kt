package ru.practicum.android.diploma.filter.di.data

import org.koin.dsl.module
import ru.practicum.android.diploma.filter.data.repositoryimpl.IndustryRepositoryImpl
import ru.practicum.android.diploma.filter.data.repositoryimpl.LocationRepositoryImpl
import ru.practicum.android.diploma.filter.domain.repository.IndustryRepository
import ru.practicum.android.diploma.filter.domain.repository.LocationRepository

val filterDataModule = module {

    single<IndustryRepository> {
        IndustryRepositoryImpl(get(), get(), get())
    }
    single<LocationRepository> {
        LocationRepositoryImpl(get(), get(), get())
    }
}
