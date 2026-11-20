package ru.practicum.android.diploma.filter.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.filter.presentation.state.FilterState

class FilterViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<FilterState>(FilterState())
    fun observeState(): LiveData<FilterState> = stateLiveData

    private val defaultState = FilterState()
    private var lastSavedState = FilterState()

    fun resetButtonClicked() {
        stateLiveData.value = FilterState()
    }

    fun fillData(state: FilterState) {
        lastSavedState = state
        stateLiveData.value = state
    }

    fun setFilterArea(id: Int, name: String, parentName: String) {
        val v = stateLiveData.value ?: return
        val filterArea = FilterArea(id, name, null, parentName = parentName)
        stateLiveData.value = FilterState(filterArea, v.filterIndustry, v.salary, v.doNotShowWithoutSalary)
    }

    fun setFilterIndustry(id: Int, name: String) {
        val v = stateLiveData.value ?: return
        val filterIndustry = FilterIndustry(id, name)
        stateLiveData.value = FilterState(v.filterArea, filterIndustry, v.salary, v.doNotShowWithoutSalary)
    }

    fun setSalary(salary: Long?) {
        val state = stateLiveData.value ?: return
        if (state.salary == salary) return
        stateLiveData.value = state.copy(salary = salary)
    }

    fun setDoNotShowWithoutSalary(doNotShowWithoutSalary: Boolean) {
        val v = stateLiveData.value ?: return
        stateLiveData.value = FilterState(v.filterArea, v.filterIndustry, v.salary, doNotShowWithoutSalary)
    }

    fun deleteLocation() {
        val state = stateLiveData.value ?: return

        stateLiveData.value = state.copy(filterArea = null)
    }

    fun deleteIndustry() {
        val state = stateLiveData.value ?: return

        stateLiveData.value = state.copy(filterIndustry = null)
    }

    fun compareWithDefault(): Boolean {
        val v = stateLiveData.value
        return !(defaultState.equals(v))
    }

    fun compareWithLast(): Boolean {
        val state = stateLiveData.value ?: return false
        return !(state.equals(lastSavedState))
    }
}
