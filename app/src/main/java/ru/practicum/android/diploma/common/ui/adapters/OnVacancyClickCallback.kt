package ru.practicum.android.diploma.common.ui.adapters

import ru.practicum.android.diploma.common.domain.models.VacancyCard

fun interface OnVacancyClickCallback {
    fun callback(vacancy: VacancyCard)
}
