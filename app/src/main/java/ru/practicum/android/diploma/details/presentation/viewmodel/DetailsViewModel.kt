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

class DetailsViewModel(
    private val detailsInteractor: DetailsInteractor,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {
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
                if (res.code == NOT_FOUND) {
                    stateLiveData.value = DetailsState.NotFound
                } else {
                    stateLiveData.value = DetailsState.OtherError(res.code)
                }
            }
        }
    }

    fun actionFavoriteClicked() {
        if (stateLiveData.value is DetailsState.Content) {
            val v: DetailsState = stateLiveData.value!!
            if (v is DetailsState.Content) { // Проверяем заново, т.к. компилятор требует однозначности
                if (v.vacancyDetail.isFavorite) {
                    viewModelScope.launch {
                        favoriteInteractor.deleteVacancy(v.vacancyDetail)
                        v.vacancyDetail.isFavorite = false
                        stateLiveData.value = v
                    }
                } else {
                    viewModelScope.launch {
                        favoriteInteractor.insertVacancy(v.vacancyDetail)
                        v.vacancyDetail.isFavorite = true
                        stateLiveData.value = v
                    }
                }
            }
        }
    }

    companion object {
        private const val NOT_FOUND = 404
    }
}
