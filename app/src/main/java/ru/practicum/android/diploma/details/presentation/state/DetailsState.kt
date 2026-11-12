package ru.practicum.android.diploma.details.presentation.state

import ru.practicum.android.diploma.details.domain.model.VacancyDetail

sealed interface DetailsState {
    object Loading : DetailsState

    data class Content(val vacancyDetail: VacancyDetail) : DetailsState

    object NotFound: DetailsState

    data class OtherError(val code: Int) : DetailsState
}
