package ru.practicum.android.diploma.filter.di.data

import org.koin.dsl.module
import ru.practicum.android.diploma.filter.data.repositoryimpl.IndustryRepositoryImpl
import ru.practicum.android.diploma.filter.domain.repository.IndustryRepository

val filterDataModule = module {

    single <IndustryRepository> {
        IndustryRepositoryImpl(get(), get(), get())
    }
}
