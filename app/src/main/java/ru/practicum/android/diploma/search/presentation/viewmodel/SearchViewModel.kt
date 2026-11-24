package ru.practicum.android.diploma.search.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.filter.domain.model.FilterSettings
import ru.practicum.android.diploma.filter.domain.usecase.FilterSettingsInteractor
import ru.practicum.android.diploma.search.domain.model.SearchPage
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor
import ru.practicum.android.diploma.search.presentation.state.SearchState
import java.io.IOException

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val settingsInteractor: FilterSettingsInteractor
) : ViewModel() {
    private val mutableState = MutableLiveData<SearchState>(SearchState.Default)
    val state: LiveData<SearchState> = mutableState

    private var currentQuery = ""
    private var searchJob: Job? = null
    private var countJob: Job? = null
    private var appliedFilters = FilterSettings()

    fun hasFilters(): Boolean = settingsInteractor.read().hasFilters

    fun retrySearch() {
        val query = currentQuery.trim()
        if (query.isEmpty()) return
        val filters = settingsInteractor.read()
        searchJob?.cancel()
        countJob?.cancel()
        searchJob = viewModelScope.launch {
            searchPage(query, FIRST_PAGE, filters)
        }
    }

    fun retrySearchWithDebounce() {
        val failedFilters = appliedFilters
        viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            if (mutableState.value == SearchState.Error.NoInternet && failedFilters == settingsInteractor.read()) {
                retrySearch()
            }
        }
    }

    fun onQueryChanged(query: String) {
        if (query == currentQuery) {
            return
        }
        currentQuery = query
        searchJob?.cancel()
        countJob?.cancel()

        val searchQuery = query.trim()
        if (searchQuery.isEmpty()) {
            mutableState.value = SearchState.Default
            return
        }

        val filters = settingsInteractor.read()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            searchPage(searchQuery, FIRST_PAGE, filters)
        }

        val content = mutableState.value as? SearchState.Content
        if (content != null && (content.isLoadingMore || content.paginationError != null)) {
            mutableState.value = content.copy(isLoadingMore = false, paginationError = null)
        }
    }

    fun loadPage(page: Int) {
        val content = mutableState.value as? SearchState.Content ?: return
        if (searchJob?.isActive == true || page != content.page + 1 || !content.hasNextPage) {
            return
        }

        val query = currentQuery.trim()
        val filters = appliedFilters
        val job =
            viewModelScope.launch(start = CoroutineStart.LAZY) { searchPage(query, page, filters, content) }
        searchJob = job
        job.start()
    }

    fun onPaginationErrorShown() {
        val content = mutableState.value as? SearchState.Content ?: return
        if (content.paginationError != null) {
            mutableState.value = content.copy(paginationError = null)
        }
    }

    private suspend fun searchPage(
        query: String,
        page: Int,
        filters: FilterSettings,
        previous: SearchState.Content? = null
    ) {
        appliedFilters = filters
        mutableState.value =
            previous?.copy(isLoadingMore = true, paginationError = null) ?: SearchState.Loading

        val countryId = filters.countrySearchId()
        val result = try {
            searchInteractor.searchVacancies(filters.searchOptions(query, page), countryId)
        } catch (_: IOException) {
            Resource.Error(NO_INTERNET)
        }
        currentCoroutineContext().ensureActive()

        when (result) {
            is Resource.Success -> showPage(result.data, countryId != null)
            is Resource.Error -> showSearchError(result.code)
        }
    }

    private fun showPage(page: SearchPage, isCountrySearch: Boolean) {
        val content = mutableState.value as? SearchState.Content
        val previousVacancies = content?.vacancies.orEmpty()
        val vacancies = if (page.vacancies.isEmpty()) {
            previousVacancies
        } else {
            (previousVacancies + page.vacancies).distinctBy { it.id }
        }
        val found = if (isCountrySearch) maxOf(page.found, content?.found ?: 0) else page.found
        mutableState.value = SearchState.Content(vacancies, found, page.page, page.hasNextPage)
        if (isCountrySearch && page.page == FIRST_PAGE && page.hasNextPage) {
            countJob = viewModelScope.launch { updateCountryCount() }
        }
    }

    private fun showSearchError(code: Int) {
        val content = mutableState.value as? SearchState.Content
        val error = if (code == NO_INTERNET) SearchState.Error.NoInternet else SearchState.Error.Server
        mutableState.value = content?.copy(isLoadingMore = false, paginationError = error) ?: error
    }

    private suspend fun updateCountryCount() {
        searchInteractor.countCountryVacancies().collect { found ->
            currentCoroutineContext().ensureActive()
            val content = mutableState.value as? SearchState.Content
            if (content != null && found > content.found) {
                mutableState.value = content.copy(found = found)
            }
        }
    }

    private fun FilterSettings.searchOptions(query: String, page: Int): Map<String, String> = buildMap {
        put("text", query)
        put("page", page.toString())
        filterArea?.let { put("area", it.id.toString()) }
        filterIndustry?.let { put("industry", it.id.toString()) }
        salary?.let { put("salary", it.toString()) }
        if (doNotShowWithoutSalary) put("only_with_salary", "true")
    }

    private fun FilterSettings.countrySearchId(): Int? = filterArea?.takeIf { it.id == it.countryId }?.id

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 2000L
        private const val FIRST_PAGE = 1
        private const val NO_INTERNET = -1
    }
}
