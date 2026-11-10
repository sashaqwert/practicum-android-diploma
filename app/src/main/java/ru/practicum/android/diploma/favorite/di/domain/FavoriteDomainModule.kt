package ru.practicum.android.diploma.favorite.di.domain

import org.koin.dsl.module
import ru.practicum.android.diploma.favorite.data.repositoryimpl.FavoriteRepositoryImpl
import ru.practicum.android.diploma.favorite.domain.repository.FavoriteRepository
import ru.practicum.android.diploma.favorite.domain.usecase.FavoriteInteractor
import ru.practicum.android.diploma.favorite.domain.usecaseimpl.FavoriteInteractorImpl

val favoriteDomainModuleDI = module {
    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get(), get())
    }
}
