package ru.practicum.android.diploma.filter.presentation.state

import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry

sealed interface FilterState {
    data class Default(val filterArea: FilterArea? = null, val filterIndustry: FilterIndustry? = null, val salary: Int? = null, val doNotShoeWithoutSalary : Boolean = false) : FilterState

    data class Changed(val filterArea: FilterArea? = null, val filterIndustry: FilterIndustry? = null, val salary: Int? = null, val doNotShoeWithoutSalary : Boolean = false) : FilterState
}
