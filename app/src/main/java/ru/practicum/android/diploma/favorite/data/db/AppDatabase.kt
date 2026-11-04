package ru.practicum.android.diploma.favorite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.android.diploma.favorite.data.db.dao.VacancyDao
import ru.practicum.android.diploma.favorite.data.db.entities.VacancyEntity

@Database(version = 1, entities = [VacancyEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun vacancyDao(): VacancyDao
}
