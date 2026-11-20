package ru.practicum.android.diploma.filter.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.usecase.CountryInteractor
import ru.practicum.android.diploma.filter.presentation.state.CountryState

class CountryViewModel(private val countryInteractor: CountryInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<CountryState>()
    fun observeState(): LiveData<CountryState> = stateLiveData

    fun fillData() {
        stateLiveData.value = CountryState.Loading
        viewModelScope.launch {
        }
    }
}
