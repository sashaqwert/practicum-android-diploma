package ru.practicum.android.diploma.details.domain.model

data class Contacts(
    val email: String,
    val id: String,
    val name: String,
    val phones: List<Phone>
)
