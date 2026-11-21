package ru.practicum.android.diploma.filter.domain.model

import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry

data class FilterSettings(
    val filterArea: FilterArea? = null,
    val filterIndustry: FilterIndustry? = null,
    val salary: Long? = null,
    val doNotShowWithoutSalary: Boolean = false
) {
    val hasFilters: Boolean
        get() = filterArea != null || filterIndustry != null || salary != null || doNotShowWithoutSalary
}
