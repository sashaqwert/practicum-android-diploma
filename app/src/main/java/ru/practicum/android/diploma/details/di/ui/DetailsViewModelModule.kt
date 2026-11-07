package ru.practicum.android.diploma.details.di.ui

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.details.presentation.view_model.DetailsViewModel

val detailsViewModelModule = module {
    viewModel {
        DetailsViewModel()
    }
}
