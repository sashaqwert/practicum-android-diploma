package ru.practicum.android.diploma.filter.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.filter.presentation.state.FilterState

class FilterViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<FilterState>(FilterState.Default())
    fun observeState(): LiveData<FilterState> = stateLiveData

    fun applyButtonClicked() {}

    fun resetButtonClicked() {
        stateLiveData.value = FilterState.Default()
    }

    fun setFilterArea(filterArea: FilterArea) {
        val v = stateLiveData.value
        if (v is FilterState.Default) {
            stateLiveData.value = FilterState.Changed(filterArea, v.filterIndustry, v.salary, v.doNotShoeWithoutSalary)
        } else if (v is FilterState.Changed) {
            stateLiveData.value = FilterState.Changed(filterArea, v.filterIndustry, v.salary, v.doNotShoeWithoutSalary)
        }
    }

    fun setFilterIndustry(filterIndustry: FilterIndustry) {
        val v = stateLiveData.value
        if (v is FilterState.Default) {
            stateLiveData.value = FilterState.Changed(v.filterArea, filterIndustry, v.salary, v.doNotShoeWithoutSalary)
        } else if (v is FilterState.Changed) {
            stateLiveData.value = FilterState.Changed(v.filterArea, filterIndustry, v.salary, v.doNotShoeWithoutSalary)
        }
    }

    fun setSalary(salary : Int?) {
        val v = stateLiveData.value
        if (v is FilterState.Default) {
            stateLiveData.value = FilterState.Changed(v.filterArea, v.filterIndustry, salary, v.doNotShoeWithoutSalary)
        } else if (v is FilterState.Changed) {
            stateLiveData.value = FilterState.Changed(v.filterArea, v.filterIndustry, salary, v.doNotShoeWithoutSalary)
        }
    }

    fun setDoNotShoeWithoutSalary(doNotShoeWithoutSalary : Boolean) {
        val v = stateLiveData.value
        if (v is FilterState.Default) {
            stateLiveData.value = FilterState.Changed(v.filterArea, v.filterIndustry, v.salary, doNotShoeWithoutSalary)
        } else if (v is FilterState.Changed) {
            stateLiveData.value = FilterState.Changed(v.filterArea, v.filterIndustry, v.salary, doNotShoeWithoutSalary)
        }
    }
}
