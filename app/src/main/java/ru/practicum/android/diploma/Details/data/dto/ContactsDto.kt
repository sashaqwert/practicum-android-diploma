package ru.practicum.android.diploma.Details.data.dto

import ru.practicum.android.diploma.Details.data.dto.PhoneDto

data class ContactsDto(
    val email: String,
    val id: String,
    val name: String,
    val phones: List<PhoneDto>
)
