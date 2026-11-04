package ru.practicum.android.diploma.common.domain

sealed interface Resource<out T> {

    data class Success<T>(
        val data: T
    ) : Resource<T>

    data class Error(
        val code: Int
    ) : Resource<Nothing>
}
