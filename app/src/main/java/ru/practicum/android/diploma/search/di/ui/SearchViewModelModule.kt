package ru.practicum.android.diploma.search.di.ui

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.search.presentation.view_model.SearchViewModel

val searchViewModelModule = module {
    viewModel { SearchViewModel(get()) }
}
