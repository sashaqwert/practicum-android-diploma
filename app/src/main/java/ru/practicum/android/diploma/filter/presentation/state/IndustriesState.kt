package ru.practicum.android.diploma.filter.presentation.state

import ru.practicum.android.diploma.common.domain.models.FilterIndustry

sealed interface IndustriesState {
    object Loading : IndustriesState

    data class Content(val industries: List<FilterIndustry>)

    object Error : IndustriesState
}
