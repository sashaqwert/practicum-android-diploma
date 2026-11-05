package ru.practicum.android.diploma.favorite.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacancy_table")
data class VacancyEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val address: String,
    val area: String,
    val contacts: String,
    val description: String,
    val employer: String,
    val employment: String,
    val experience: String,
    val industry: String,
    val name: String,
    val salary: String,
    val schedule: String,
    val skills: String,
    val url: String
)
