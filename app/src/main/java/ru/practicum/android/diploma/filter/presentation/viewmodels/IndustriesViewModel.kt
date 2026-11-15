package ru.practicum.android.diploma.filter.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.presentation.state.IndustriesState

class IndustriesViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<IndustriesState>()
    fun observeState(): LiveData<IndustriesState> = stateLiveData

    fun fillData() {
        stateLiveData.value = IndustriesState.Loading
        viewModelScope.launch {
        }
    }
}
