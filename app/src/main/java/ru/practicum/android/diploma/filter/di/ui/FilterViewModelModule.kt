package ru.practicum.android.diploma.filter.di.ui

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.filter.presentation.viewmodels.IndustriesViewModel

val filterViewModelModule = module {
    viewModel {
        IndustriesViewModel()
    }
}
