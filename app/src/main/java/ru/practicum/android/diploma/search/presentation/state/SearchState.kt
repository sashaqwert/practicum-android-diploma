package ru.practicum.android.diploma.search.presentation.state

import ru.practicum.android.diploma.common.domain.models.VacancyCard

sealed interface SearchState {
    object Default : SearchState

    object Loading : SearchState

    data class Content(val vacancies: List<VacancyCard>, val found: Int) : SearchState

    object Error : SearchState
}
