package ru.practicum.android.diploma.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.search.domain.model.SearchResult
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

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            mutableState.value = SearchState.Loading
            try {
                val result = searchInteractor.searchVacancies(mapOf("text" to searchQuery)) as SearchResult
                ensureActive()
                mutableState.value = SearchState.Content(result.vacancies, result.found)
            } catch (_: IOException) {
                ensureActive()
                mutableState.value = SearchState.Error
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 2000L
    }
}
