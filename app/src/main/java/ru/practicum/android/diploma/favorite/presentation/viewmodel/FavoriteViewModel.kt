package ru.practicum.android.diploma.favorite.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.favorite.domain.usecase.FavoriteInteractor
import ru.practicum.android.diploma.favorite.presentation.state.FavoriteState

class FavoriteViewModel(favoriteInteractor: FavoriteInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoriteState>()
    fun observeState(): LiveData<FavoriteState> = stateLiveData

    fun fillData() {
        stateLiveData.value = FavoriteState.Loading
        viewModelScope.launch {
            // Здесь должен быть вызов интерактора
        }
    }
}
