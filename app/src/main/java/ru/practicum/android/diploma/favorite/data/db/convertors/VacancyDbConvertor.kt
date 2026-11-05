package ru.practicum.android.diploma.favorite.data.db.convertors

import com.google.gson.Gson
import ru.practicum.android.diploma.favorite.data.db.entities.VacancyEntity
import ru.practicum.android.diploma.details.data.response.VacancyDetailResponse

class VacancyDbConvertor(private val gson: Gson) {
    fun map(vacancy: VacancyDetailResponse): VacancyEntity =
        VacancyEntity(
            id = vacancy.id,
            address = gson.toJson(vacancy.address),
            area = gson.toJson(vacancy.area),
            contacts = gson.toJson(vacancy.contacts),
            description = vacancy.description,
            employer = gson.toJson(vacancy.employer),
            employment = gson.toJson(vacancy.employment),
            experience = gson.toJson(vacancy.experience),
            industry = gson.toJson(vacancy.industry),
            name = vacancy.name,
            salary = gson.toJson(vacancy.salary),
            schedule = gson.toJson(vacancy.schedule),
            skills = gson.toJson(vacancy.skills),
            url = vacancy.url
        )

    // Потом сюда нужно будет добавить обратный конвертер тому, кто будет заниматься экраном избранных вакансий
}
