package ru.practicum.android.diploma.filter.presentation.state

import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry

data class FilterState(
    val filterArea: FilterArea? = null,
    val filterIndustry: FilterIndustry? = null,
    val salary: Long? = null,
    val doNotShowWithoutSalary: Boolean = false
)
