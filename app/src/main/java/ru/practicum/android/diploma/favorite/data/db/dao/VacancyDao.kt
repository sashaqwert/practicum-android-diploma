package ru.practicum.android.diploma.favorite.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.favorite.data.db.entities.VacancyEntity

@Dao
interface VacancyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVacancy(vacancyEntity: VacancyEntity)

    @Delete
    suspend fun deleteVacancy(vacancyEntity: VacancyEntity)

    @Query("SELECT * FROM vacancy_table")
    fun getAllVacancies(): Flow<List<VacancyEntity>>

    @Query("SELECT * FROM vacancy_table WHERE id = :id")
    suspend fun getVacancyById(id: String): VacancyEntity?

    @Query("DELETE FROM vacancy_table WHERE id = :id")
    suspend fun deleteVacancyById(id: String): Int
}
