package ru.practicum.android.diploma.search.presentation.state

import ru.practicum.android.diploma.common.domain.models.VacancyCard

sealed interface SearchState {
    object Default : SearchState

    object Loading : SearchState

    data class Content(
        val vacancies: List<VacancyCard>,
        val found: Int,
        val page: Int,
        val pages: Int,
        val isLoadingMore: Boolean = false,
        val paginationError: Error? = null
    ) : SearchState

    sealed interface Error : SearchState {
        object NoInternet : Error

        object Server : Error
    }
}
