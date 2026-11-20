package ru.practicum.android.diploma.search.presentation.viewmodel

import android.util.Log
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
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor
import ru.practicum.android.diploma.search.presentation.state.SearchState
import java.io.IOException
import kotlin.collections.filterKeys

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {
    private val mutableState = MutableLiveData<SearchState>(SearchState.Default)
    val state: LiveData<SearchState> = mutableState

    private var currentQuery = ""
    private var searchJob: Job? = null
    private var savedOptions = mutableMapOf<String, String>()
    private var savedOptionsNames = mutableMapOf<String, String>()

    fun retrySearch() {
        val query = currentQuery.trim()
        if (query.isEmpty()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchPage(query, FIRST_PAGE)
        }
    }

    fun retrySearchWithDebounce() {
        viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            retrySearch()
        }
    }

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
            searchPage(searchQuery, FIRST_PAGE)
        }

        val content = mutableState.value as? SearchState.Content
        if (content != null && (content.isLoadingMore || content.paginationError != null)) {
            mutableState.value = content.copy(isLoadingMore = false, paginationError = null)
        }
    }

    fun loadPage(page: Int) {
        val content = mutableState.value as? SearchState.Content ?: return
        if (searchJob?.isActive == true || page != content.page + 1 || page >= content.pages) {
            return
        }

        val query = currentQuery.trim()
        val job =
            viewModelScope.launch(start = CoroutineStart.LAZY) { searchPage(query, page, content) }
        searchJob = job
        job.start()
    }

    fun onPaginationErrorShown() {
        val content = mutableState.value as? SearchState.Content ?: return
        if (content.paginationError != null) {
            mutableState.value = content.copy(paginationError = null)
        }
    }

    fun searchWithFilters(
        areaId: Int? = null,
        industryId: Int? = null,
        salary: Long? = null,
        doNotShowWithSalary: Boolean? = null
    ) {
        val query = currentQuery.trim()
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            searchPage(
                query,
                FIRST_PAGE,
                areaId = areaId,
                industryId = industryId,
                salary = salary,
                doNotShowWithSalary = doNotShowWithSalary,
                searchFromFilter = true
            )
        }
    }

    fun getSavedOptions(): Map<String, String> {
        return savedOptions
    }

    fun getSavedOptionsNames(): Map<String, String> {
        return savedOptionsNames
    }

    fun saveOptionsName(areaName: String, industryName: String) {
        savedOptionsNames["area"] = areaName
        savedOptionsNames["industry"] = industryName
    }

    private suspend fun searchPage(
        query: String,
        page: Int,
        previous: SearchState.Content? = null,
        areaId: Int? = null,
        industryId: Int? = null,
        salary: Long? = null,
        doNotShowWithSalary: Boolean? = null,
        searchFromFilter: Boolean = false
    ) {
        mutableState.value =
            previous?.copy(isLoadingMore = true, paginationError = null) ?: SearchState.Loading
        
        var options = mutableMapOf("text" to query, "page" to page.toString())

        if (searchFromFilter) {
            areaId?.let {
                options["area"] = it.toString()
                savedOptions["area"] = it.toString()
            }
            industryId?.let {
                options["industry"] = it.toString()
                savedOptions["industry"] = it.toString()
            }
            salary?.let {
                options["salary"] = it.toString()
                savedOptions["salary"] = it.toString()
            }
            doNotShowWithSalary?.let {
                options["only_with_salary"] = it.toString()
                savedOptions["only_with_salary"] = it.toString()
            }
        } else {
            options += savedOptions
        }

        val result = try {
            searchInteractor.searchVacancies(options)
        } catch (_: IOException) {
            Resource.Error(NO_INTERNET)
        }
        currentCoroutineContext().ensureActive()

        mutableState.value = when (result) {
            is Resource.Success -> {
                val data = result.data
                val vacancies =
                    (previous?.vacancies.orEmpty() + data.vacancies).distinctBy { it.id }
                SearchState.Content(vacancies, data.found, data.page, data.pages)
            }

            is Resource.Error -> {
                val error =
                    if (result.code == NO_INTERNET) SearchState.Error.NoInternet else SearchState.Error.Server
                previous?.copy(isLoadingMore = false, paginationError = error) ?: error
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 2000L
        private const val FIRST_PAGE = 0
        private const val NO_INTERNET = -1
    }
}
