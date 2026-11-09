package ru.practicum.android.diploma.details.ui.util

import android.content.res.Resources
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import java.text.NumberFormat
import java.util.Locale

fun getTextSalary(vacancyDetail: VacancyDetail, resources: Resources): String {
    return vacancyDetail.salary?.let { salary ->
        val formatter = NumberFormat.getIntegerInstance(Locale("ru", "RU"))
        val formattedSalaryFrom = salary.from?.let { formatter.format(it) } ?: ""
        val formattedSalaryTo = salary.to?.let { formatter.format(it) } ?: ""

        val currency = getCurrencySymbol(vacancyDetail.salary.currency ?: "")

        val salaryFrom =
            if (salary.from != null) {
                resources.getString(
                    R.string.salary_from,
                    formattedSalaryFrom,
                    currency
                ) + " "
            } else {
                ""
            }
        val salaryTo = if (salary.to != null) {
            resources.getString(
                R.string.salary_to,
                formattedSalaryTo,
                currency
            )
        } else {
            ""
        }

        "$salaryFrom$salaryTo"
    } ?: ""
}
