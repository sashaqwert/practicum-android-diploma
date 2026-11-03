package ru.practicum.android.diploma.search.presentation

import ru.practicum.android.diploma.search.domain.model.Vacancy

sealed interface SearchState {
    object Initial : SearchState

    object Loading : SearchState

    data class Content(val vacancies: List<Vacancy>, val found: Int) : SearchState

    object Error : SearchState
}
