package ru.practicum.android.diploma.favorite.data.repositoryimpl

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.common.data.converters.Converters
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.favorite.data.db.dao.VacancyDao
import ru.practicum.android.diploma.favorite.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val vacancyDao: VacancyDao,
    private val converters: Converters
) : FavoriteRepository {
    override fun getAllVacancies(): Flow<Resource<List<VacancyDetail>>> =
        try {
            vacancyDao.getAllVacancies().map { vacancyEntities ->
                Resource.Success(vacancyEntities.map { converters.entityToDetail(it) }.reversed())
            }
        } catch (_: SQLiteException) {
            flowOf(Resource.Error(SQLITE_EXCEPTION))
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

    override suspend fun getVacancyById(id: String): Resource<VacancyDetail> {
        try {
            val vacancy = withContext(Dispatchers.IO) {
                vacancyDao.getVacancyById(id)
            }
            val response = if (vacancy != null) {
                Resource.Success(converters.entityToDetail(vacancy))
            } else {
                Resource.Error(NOT_FOUND)
            }
            return response
        } catch (_: SQLiteException) {
            return Resource.Error(SQLITE_EXCEPTION)
        }
    }

    companion object {
        private const val SQLITE_EXCEPTION = 1
        private const val NOT_FOUND = 2
    }
}
