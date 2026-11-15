package ru.practicum.android.diploma.favorite.di.domain

import org.koin.dsl.module
import ru.practicum.android.diploma.favorite.domain.usecase.FavoriteInteractor
import ru.practicum.android.diploma.favorite.domain.usecaseimpl.FavoriteInteractorImpl

val favoriteDomainModuleDI = module {
    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get(), get())
    }
}
