package ru.practicum.android.diploma.common.data.dto.details

data class ContactsDto(
    val email: String,
    val id: String,
    val name: String,
    val phones: List<PhoneDto>
)
