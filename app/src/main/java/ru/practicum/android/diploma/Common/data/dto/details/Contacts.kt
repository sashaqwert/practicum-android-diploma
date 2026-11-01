package ru.practicum.android.diploma.Common.data.dto.details

data class Contacts(
    val email: String,
    val id: String,
    val name: String,
    val phones: List<Phone>
)
