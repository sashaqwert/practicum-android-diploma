package ru.practicum.android.diploma.filter.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.presentation.state.RegionState

class RegionViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<RegionState>()
    fun observeState(): LiveData<RegionState> = stateLiveData

    fun fillData() {
        stateLiveData.value = RegionState.Loading
        viewModelScope.launch {
        }
    }
}
