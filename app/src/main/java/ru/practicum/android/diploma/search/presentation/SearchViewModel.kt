package ru.practicum.android.diploma.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.search.domain.model.SearchResult
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor
import java.io.IOException

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {
    private val mutableState = MutableLiveData<SearchState>(SearchState.Default)
    val state: LiveData<SearchState> = mutableState

    private var currentQuery = ""
    private var searchJob: Job? = null
    private var currentPage = FIRST_PAGE
    private var maxPages = 0
    private var found = 0
    private val vacancies = mutableListOf<VacancyCard>()

    fun onQueryChanged(query: String) {
        if (query == currentQuery) {
            return
        }
        currentQuery = query
        searchJob?.cancel()
        resetPagination()

        val searchQuery = query.trim()
        if (searchQuery.isEmpty()) {
            mutableState.value = SearchState.Default
            return
        }

        resetFooterState()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            searchVacancies(searchQuery, FIRST_PAGE)
        }
    }

    fun onBottomReached() {
        if (searchJob?.isActive == true || currentPage >= maxPages) {
            return
        }
        val searchQuery = currentQuery.trim()
        if (searchQuery.isEmpty()) {
            return
        }
        val page = currentPage
        searchJob = viewModelScope.launch {
            searchVacancies(searchQuery, page)
        }
    }

    fun onPaginationErrorShown() {
        val content = mutableState.value as? SearchState.Content ?: return
        if (content.paginationErrorCode != null) {
            mutableState.value = content.copy(paginationErrorCode = null)
        }
    }

    private fun resetPagination() {
        currentPage = FIRST_PAGE
        maxPages = 0
        found = 0
        vacancies.clear()
    }

    private fun resetFooterState() {
        val content = mutableState.value as? SearchState.Content ?: return
        if (content.isLoadingMore || content.paginationErrorCode != null) {
            mutableState.value = content.copy(isLoadingMore = false, paginationErrorCode = null)
        }
    }

    private suspend fun searchVacancies(query: String, page: Int) {
        if (page == FIRST_PAGE) {
            mutableState.value = SearchState.Loading
        } else {
            renderContent(isLoadingMore = true)
        }
        try {
            val result = searchInteractor.searchVacancies(query, page)
            currentCoroutineContext().ensureActive()
            when (result) {
                is Resource.Success -> applyResult(result.data)
                is Resource.Error -> renderError(page, result.code)
            }
        } catch (_: IOException) {
            currentCoroutineContext().ensureActive()
            renderError(page, NO_INTERNET)
        }
    }

    private fun applyResult(result: SearchResult) {
        val knownIds = vacancies.mapTo(mutableSetOf()) { it.id }
        vacancies.addAll(result.vacancies.filter { knownIds.add(it.id) })
        found = result.found
        currentPage = result.page + 1
        maxPages = result.pages
        renderContent()
    }

    private fun renderError(page: Int, code: Int) {
        if (page == FIRST_PAGE) {
            mutableState.value = SearchState.Error(code)
        } else {
            renderContent(errorCode = code)
        }
    }

    private fun renderContent(isLoadingMore: Boolean = false, errorCode: Int? = null) {
        mutableState.value = SearchState.Content(vacancies.toList(), found, isLoadingMore, errorCode)
    }

    companion object {
        private const val FIRST_PAGE = 0
        private const val SEARCH_DEBOUNCE_MILLIS = 2000L
        private const val NO_INTERNET = -1
    }
}
