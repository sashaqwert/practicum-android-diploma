package ru.practicum.android.diploma.details.ui.util

import android.content.res.Resources
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import java.text.NumberFormat
import java.util.Locale

fun getTextSalary(vacancyDetail: VacancyDetail, resources: Resources): String {
    val salary = vacancyDetail.salary ?: return ""
    val formatter = NumberFormat.getIntegerInstance(Locale("ru", "RU"))
    val currency = getCurrencySymbol(salary.currency.orEmpty())

    val salaryFrom = salary.from?.let { amount ->
        if (salary.to != null) {
            resources.getString(R.string.salary_from_without_to, formatter.format(amount))
        } else {
            resources.getString(R.string.salary_from, formatter.format(amount), currency)
        }
    }

    val salaryTo = salary.to?.let { amount ->
        resources.getString(R.string.salary_to, formatter.format(amount), currency)
    }

    return listOfNotNull(salaryFrom, salaryTo).joinToString(" ")
}
