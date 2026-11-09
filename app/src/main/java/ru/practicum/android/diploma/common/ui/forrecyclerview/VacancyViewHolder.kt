package ru.practicum.android.diploma.common.ui.forrecyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.TypedValueCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.databinding.ItemVacancyBinding

class VacancyViewHolder(private val binding: ItemVacancyBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(vacancy: VacancyCard) {
        binding.vacationName.text = vacancy.name
        if (vacancy.company != null) {
            binding.industry.text = vacancy.company
        } else {
            binding.industry.text = ""
        }
        if (vacancy.salary != null) {
            if ((vacancy.salary.to != null) or (vacancy.salary.from != null)) {
                var salary = ""
                if (vacancy.salary.from != null) {
                    salary += itemView.context.getString(R.string.from) + " "
                    salary += formatNumber(vacancy.salary.from) + " "
                }
                if (vacancy.salary.to != null) {
                    salary += itemView.context.getString(R.string.to) + " "
                    salary += formatNumber(vacancy.salary.to) + " "
                }
                val currency = formatCurrency(vacancy.salary.currency)
                binding.salary.text = salary + currency
            } else {
                binding.salary.setText(R.string.salary_not_set)
            }
        } else {
            binding.salary.setText(R.string.salary_not_set)
        }

        if (vacancy.logo != null) {
            binding.imageRecyclerView.load(vacancy.logo) {
                placeholder(R.drawable.img_empty_logo_48_xml)
                error(R.drawable.img_empty_logo_48_xml)
                transformations(
                    RoundedCornersTransformation(
                        radius = TypedValueCompat.dpToPx(CORNER_RADIUS_DP, itemView.resources.displayMetrics)
                    )
                )

                listener(
                    onError = { _, result ->
                        Log.e("CoilError", "Failed to load: ${vacancy.logo}", result.throwable)
                    }
                )
            }
        } else {
            binding.imageRecyclerView.setImageResource(R.drawable.img_empty_logo_48_xml)
        }
    }

    private fun formatNumber(value: Int): String {
        return value.toString()
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()
    }

    private fun formatCurrency(currency: String?): String {
        if (currency.isNullOrEmpty()) {
            return ""
        } else {
            return when (currency) {
                "RUR", "RUB" -> "₽"
                "BYR" -> "Br"
                "USD" -> "$"
                "EUR" -> "€"
                "KZT" -> "₸"
                "UAH" -> "₴"
                "AZN" -> "₼"
                "UZS" -> "so'm"
                "GEL" -> "₾"
                "KGT" -> "сом"
                else -> ""
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): VacancyViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemVacancyBinding.inflate(inflater, parent, false)
            return VacancyViewHolder(binding)
        }

        const val CORNER_RADIUS_DP = 12f
    }
}
