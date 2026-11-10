package ru.practicum.android.diploma.details.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.domain.usecase.DetailsInteractor
import ru.practicum.android.diploma.details.presentation.state.DetailsState
import ru.practicum.android.diploma.favorite.domain.usecase.FavoriteInteractor

class DetailsViewModel(private val detailsInteractor: DetailsInteractor, private val favoriteInteractor: FavoriteInteractor) : ViewModel() {
    private var id: String = "0"
    private val stateLiveData = MutableLiveData<DetailsState>()
    fun observeState(): LiveData<DetailsState> = stateLiveData

    fun setID(id: String) {
        this.id = id
    }

    fun fillData() {
        stateLiveData.value = DetailsState.Loading
        viewModelScope.launch {
            val res = detailsInteractor.getDetailsInfo(id)
            if (res is Resource.Success) {
                val v = res.data
                stateLiveData.value = DetailsState.Content(v)
            } else if (res is Resource.Error) {
                stateLiveData.value = DetailsState.Error(res.code)
            }
        }
    }

    fun actionFavoriteClicked() {
        // Тут логика обработки нажатия сердечка
    }
}
