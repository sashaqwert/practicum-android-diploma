package ru.practicum.android.diploma.favorite.di.ui

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorite.presentation.viewmodel.FavoriteViewModel

val favoriteViewModelModuleDI = module {
    viewModel {
        FavoriteViewModel(get())
    }
}
