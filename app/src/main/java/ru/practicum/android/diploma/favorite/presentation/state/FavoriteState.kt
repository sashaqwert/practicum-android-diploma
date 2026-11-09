package ru.practicum.android.diploma.favorite.presentation.state

import ru.practicum.android.diploma.common.domain.models.VacancyCard

sealed interface FavoriteState {
    object Loading : FavoriteState

    object Empty : FavoriteState

    data class Content(val vacancyes : List<VacancyCard>) : FavoriteState

    object Error : FavoriteState
}
