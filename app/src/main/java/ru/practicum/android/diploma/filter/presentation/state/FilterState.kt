package ru.practicum.android.diploma.filter.presentation.state

import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry

sealed interface FilterState {
    data class Default(val filterArea: FilterArea? = null, val filterIndustry: FilterIndustry? = null) : FilterState

    data class Changed(val filterArea: FilterArea, val filterIndustry: FilterIndustry) : FilterState
}
