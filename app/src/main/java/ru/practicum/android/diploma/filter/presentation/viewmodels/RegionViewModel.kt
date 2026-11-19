package ru.practicum.android.diploma.filter.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.filter.domain.usecase.RegionInteractor
import ru.practicum.android.diploma.filter.presentation.state.RegionState

class RegionViewModel(private val regionInteractor: RegionInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<RegionState>()
    private var allRegions = emptyList<FilterArea>()
    private var searchQuery = ""
    private var loadJob: Job? = null

    fun observeState(): LiveData<RegionState> = stateLiveData

    fun fillData(countryId: Int? = null) {
        loadJob?.cancel()
        allRegions = emptyList()
        stateLiveData.value = RegionState.Loading

        loadJob = viewModelScope.launch {
            val result = regionInteractor.getRegions(countryId)
            ensureActive()

            when (result) {
                is Resource.Success -> {
                    allRegions = result.data.toList()
                    showRegions()
                }

                is Resource.Error -> stateLiveData.value = RegionState.Error
            }
        }
    }

    fun filterRegions(query: String) {
        searchQuery = query.trim()
        if (stateLiveData.value is RegionState.Content || stateLiveData.value is RegionState.Empty) {
            showRegions()
        }
    }

    private fun showRegions() {
        val regions = allRegions.filter { region ->
            region.name.contains(searchQuery, ignoreCase = true)
        }
        stateLiveData.value = if (regions.isEmpty()) {
            RegionState.Empty
        } else {
            RegionState.Content(regions)
        }
    }
}
