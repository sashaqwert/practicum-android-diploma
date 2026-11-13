package ru.practicum.android.diploma.favorite.di.data

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.favorite.data.db.AppDatabase
import ru.practicum.android.diploma.favorite.data.repositoryimpl.FavoriteRepositoryImpl
import ru.practicum.android.diploma.favorite.domain.repository.FavoriteRepository

val favoriteDataModuleDI = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single {
        get<AppDatabase>().vacancyDao()
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }
}
