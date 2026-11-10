package ru.practicum.android.diploma.favorite.domain.usecaseimpl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.favorite.domain.repository.FavoriteRepository
import ru.practicum.android.diploma.favorite.domain.usecase.FavoriteInteractor

class FavoriteInteractorImpl(val repository: FavoriteRepository) : FavoriteInteractor {

    override fun getAllVacancies(): Flow<List<VacancyDetail>> {
        return repository.getAllVacancies()
    }

    override suspend fun insertVacancy(vacancyDetail: VacancyDetail) {
        repository.insertVacancy(vacancyDetail)
    }

    override suspend fun deleteVacancy(vacancyDetail: VacancyDetail) {
        repository.deleteVacancy(vacancyDetail)
    }
}
