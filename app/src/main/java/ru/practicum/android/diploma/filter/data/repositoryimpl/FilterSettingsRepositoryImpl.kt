package ru.practicum.android.diploma.filter.data.repositoryimpl

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.filter.domain.model.FilterSettings
import ru.practicum.android.diploma.filter.domain.repository.FilterSettingsRepository

class FilterSettingsRepositoryImpl(
    private val preferences: SharedPreferences,
    private val gson: Gson
) : FilterSettingsRepository {
    override fun read(): FilterSettings =
        preferences.getString(KEY_FILTER_STATE, null)
            ?.let { gson.fromJson(it, FilterSettings::class.java) }
            ?: FilterSettings()

    override fun save(state: FilterSettings) {
        val editor = preferences.edit()
        if (state.hasFilters) {
            editor.putString(KEY_FILTER_STATE, gson.toJson(state))
        } else {
            editor.remove(KEY_FILTER_STATE)
        }
        editor.apply()
    }

    private companion object {
        const val KEY_FILTER_STATE = "filter_state"
    }
}
