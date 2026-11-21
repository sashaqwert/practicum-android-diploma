package ru.practicum.android.diploma.filter.presentation.state

import ru.practicum.android.diploma.common.domain.models.FilterArea

sealed interface CountryState {
    object Loading : CountryState

    data class Content(val filterAreas: List<FilterArea>) : CountryState

    data class Error(val code: Int) : CountryState
}
