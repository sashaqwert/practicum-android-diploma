package ru.practicum.android.diploma.details.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
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
            val res = getVacancy()
            if (res is Resource.Success) {
                val v = res.data
                stateLiveData.value = DetailsState.Content(v)
            } else if (res is Resource.Error) {
                stateLiveData.value = DetailsState.Error(res.code)
            }
    }

    private fun getVacancy() : Resource<VacancyDetail> {
        var res : Resource<VacancyDetail> = Resource.Error(-123) // Просто дефолтное значение, чтобы не nullable
        viewModelScope.launch {
            res = detailsInteractor.getDetailsInfo(id)
        }
        return res
    }

    fun actionFavoriteClicked() {
        val r = getVacancy()
        if (r is Resource.Success) {
            if (r.data.isFavorite) {
                viewModelScope.launch {
                    favoriteInteractor.deleteVacancy(r.data)
                }
            } else {
                viewModelScope.launch {
                    favoriteInteractor.insertVacancy(r.data)
                }
            }
        }
    }
}
