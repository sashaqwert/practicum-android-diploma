package ru.practicum.android.diploma.details.ui.util

fun getCurrencySymbol(currency: String): String {
    return when (currency) {
        "RUR", "RUB" -> "₽"
        "BYR", "BYN" -> "Br"
        "USD" -> "$"
        "EUR" -> "€"
        "KZT" -> "₸"
        "UAH" -> "₴"
        "AZN" -> "₼"
        "UZS" -> "сум"
        "GEL" -> "₾"
        "KGT", "KGS" -> "сом"
        else -> currency
    }
}
