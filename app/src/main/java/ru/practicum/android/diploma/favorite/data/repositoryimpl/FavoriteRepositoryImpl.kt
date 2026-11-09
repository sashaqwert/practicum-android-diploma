package ru.practicum.android.diploma.favorite.data.repositoryimpl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.favorite.data.db.dao.VacancyDao
import ru.practicum.android.diploma.favorite.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val vacancyDao: VacancyDao,
    private val converters: Converters
) : FavoriteRepository {
    override fun getAllVacancies(): Flow<List<VacancyDetail>> =
        vacancyDao.getAllVacancies().map { vacancyEntities ->
            vacancyEntities.map { converters.entityToDetail(it) }.reversed()
        }

    override suspend fun insertVacancy(vacancyDetail: VacancyDetail) {
        vacancyDao.insertVacancy(
            converters.detailToEntity(vacancyDetail)
        )
    }

    override suspend fun deleteVacancy(vacancyDetail: VacancyDetail) {
        vacancyDao.deleteVacancy(
            converters.detailToEntity(vacancyDetail)
        )
    }
}
