package ru.practicum.android.diploma.favorite.domain.usecaseimpl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.favorite.domain.repository.FavoriteRepository
import ru.practicum.android.diploma.favorite.domain.usecase.FavoriteInteractor

class FavoriteInteractorImpl(val repository: FavoriteRepository, val converter: Converters) :
    FavoriteInteractor {

    override fun getAllVacancies(): Flow<Resource<List<VacancyCard>>> {
        return repository.getAllVacancies().map { resource ->
            when (resource) {
                is Resource.Success<List<VacancyDetail>> -> {
                    val details = resource.data
                    Resource.Success(converter.fromDetailToCard(details))
                }

                else -> {
                    Resource.Error(SQLITE_EXCEPTION)
                }
            }
        }
    }

    override suspend fun insertVacancy(vacancyDetail: VacancyDetail) {
        repository.insertVacancy(vacancyDetail)
    }

    override suspend fun deleteVacancy(vacancyDetail: VacancyDetail) {
        repository.deleteVacancy(vacancyDetail)
    }

    override suspend fun getVacancyById(id: String): Resource<VacancyDetail> {
        return repository.getVacancyById(id)
    }

    companion object {
        private const val SQLITE_EXCEPTION = 1
    }
}
