package ru.practicum.android.diploma.filter.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.filter.domain.usecase.IndustryInteractor
import ru.practicum.android.diploma.filter.presentation.state.IndustriesState

class IndustriesViewModel(private val industryInteractor: IndustryInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<IndustriesState>()
    fun observeState(): LiveData<IndustriesState> = stateLiveData

    fun fillData() {
        stateLiveData.value = IndustriesState.Loading
        viewModelScope.launch {
            val resource = industryInteractor.getIndustries()

            when (resource) {
                is Resource.Error -> {
                    stateLiveData.value = IndustriesState.Error(resource.code)
                }
                is Resource.Success -> {
                    stateLiveData.value = IndustriesState.Content(resource.data)
                }
            }
        }
    }
}
