package ru.practicum.android.diploma.filter.presentation.state

import ru.practicum.android.diploma.common.domain.models.FilterArea

sealed interface RegionState {
    object Loading : RegionState

    data class Content(val regions: List<FilterArea>) : RegionState

    object Empty : RegionState

    object Error : RegionState
}
