package ru.practicum.android.diploma.filter.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.filter.domain.model.FilterSettings
import ru.practicum.android.diploma.filter.domain.usecase.FilterSettingsInteractor
import ru.practicum.android.diploma.filter.presentation.state.FilterState

class FilterViewModel(private val settingsInteractor: FilterSettingsInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData(
        settingsInteractor.read().let {
            FilterState(it.filterArea, it.filterIndustry, it.salary, it.doNotShowWithoutSalary)
        }
    )
    fun observeState(): LiveData<FilterState> = stateLiveData

    private val defaultState = FilterState()

    fun resetButtonClicked() {
        saveState(FilterState())
    }

    fun setFilterArea(id: Int, name: String, parentName: String?, countryId: Int?) {
        val v = stateLiveData.value ?: return
        val filterArea = FilterArea(id, name, null, parentName = parentName, countryId = countryId)
        saveState(FilterState(filterArea, v.filterIndustry, v.salary, v.doNotShowWithoutSalary))
    }

    fun setFilterIndustry(id: Int, name: String) {
        val v = stateLiveData.value ?: return
        val filterIndustry = FilterIndustry(id, name)
        saveState(FilterState(v.filterArea, filterIndustry, v.salary, v.doNotShowWithoutSalary))
    }

    fun setSalary(salary: Long?) {
        val state = stateLiveData.value ?: return
        saveState(state.copy(salary = salary))
    }

    fun setDoNotShowWithoutSalary(doNotShowWithoutSalary: Boolean) {
        val v = stateLiveData.value ?: return
        saveState(FilterState(v.filterArea, v.filterIndustry, v.salary, doNotShowWithoutSalary))
    }

    fun deleteLocation() {
        val state = stateLiveData.value ?: return

        saveState(state.copy(filterArea = null))
    }

    fun deleteIndustry() {
        val state = stateLiveData.value ?: return

        saveState(state.copy(filterIndustry = null))
    }

    fun compareWithDefault(): Boolean {
        val v = stateLiveData.value
        return !(defaultState.equals(v))
    }

    private fun saveState(state: FilterState) {
        if (state == stateLiveData.value) return
        settingsInteractor.save(
            FilterSettings(state.filterArea, state.filterIndustry, state.salary, state.doNotShowWithoutSalary)
        )
        stateLiveData.value = state
    }
}
