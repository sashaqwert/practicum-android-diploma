package ru.practicum.android.diploma.details.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.details.presentation.state.DetailsState

class DetailsViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<DetailsState>()
    fun observeState(): LiveData<DetailsState> = stateLiveData

    fun fillData() {
    stateLiveData.value = DetailsState.Loading
    // FIXME: нет интерактора
    }

    fun action_favorite_clicked() {
        // FIXME: Нет интерактора
    }
}
