package ru.practicum.android.diploma.details.di.data

import org.koin.dsl.module
import ru.practicum.android.diploma.details.data.repositoryimpl.DetailsRepositoryImpl
import ru.practicum.android.diploma.details.domain.repository.DetailsRepository

val detailsDataModule = module {
    single<DetailsRepository> {
        DetailsRepositoryImpl(get(), get(), get())
    }
}
