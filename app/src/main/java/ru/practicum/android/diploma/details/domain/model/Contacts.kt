package ru.practicum.android.diploma.details.domain.model

import ru.practicum.android.diploma.details.data.dto.PhoneDto

data class Contacts(
    val email: String,
    val id: String,
    val name: String,
    val phones: List<PhoneDto>
)
