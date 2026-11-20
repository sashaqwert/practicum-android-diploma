package ru.practicum.android.diploma.filter.di.ui

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.filter.presentation.viewmodels.CountryViewModel
import ru.practicum.android.diploma.filter.presentation.viewmodels.FilterViewModel
import ru.practicum.android.diploma.filter.presentation.viewmodels.IndustriesViewModel
import ru.practicum.android.diploma.filter.presentation.viewmodels.RegionViewModel

val filterViewModelModule = module {
    viewModel {
        FilterViewModel()
    }

    viewModel {
        IndustriesViewModel(get())
    }

    viewModel {
        CountryViewModel(get())
    }

    viewModel {
        RegionViewModel(get())
    }
}
