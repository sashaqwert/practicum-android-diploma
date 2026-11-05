package ru.practicum.android.diploma.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor
import java.io.IOException

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {
    private val mutableState = MutableLiveData<SearchState>(SearchState.Default)
    val state: LiveData<SearchState> = mutableState

    private var currentQuery = ""
    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        if (query == currentQuery) {
            return
        }
        currentQuery = query
        searchJob?.cancel()

        val searchQuery = query.trim()
        if (searchQuery.isEmpty()) {
            mutableState.value = SearchState.Default
            return
        }

        mutableState.value = SearchState.Typing
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            mutableState.value = SearchState.Loading
            try {
                val result = searchInteractor.searchVacancies(searchQuery)
                ensureActive()
                mutableState.value = when (result) {
                    is Resource.Success -> SearchState.Content(result.data.vacancies, result.data.found)
                    is Resource.Error -> SearchState.Error(
                        if (result.code == NO_INTERNET) {
                            SearchState.ErrorType.NO_INTERNET
                        } else {
                            SearchState.ErrorType.SERVER
                        }
                    )
                }
            } catch (_: IOException) {
                ensureActive()
                mutableState.value = SearchState.Error(SearchState.ErrorType.SERVER)
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 2000L
        private const val NO_INTERNET = -1
    }
}
