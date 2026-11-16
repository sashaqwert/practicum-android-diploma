package ru.practicum.android.diploma.common.ui.forrecyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.decode.SvgDecoder
import coil.load
import coil.size.Scale
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.common.domain.models.VacancyCardSalary
import ru.practicum.android.diploma.databinding.ItemVacancyBinding

class VacancyViewHolder(private val binding: ItemVacancyBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(vacancy: VacancyCard) {
        binding.vacationName.text = vacancy.name
        if (vacancy.company != null) {
            binding.industry.text = vacancy.company
        } else {
            binding.industry.text = ""
        }
        setSalary(vacancy.salary)
        binding.imageRecyclerView.clipToOutline = true
        setLogo(vacancy.logo)
    }

    private fun formatNumber(value: Int): String {
        return value.toString()
            .reversed()
            .chunked(CHUNKED)
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
                "UZS" -> "сум"
                "GEL" -> "₾"
                "KGT" -> "сом"
                else -> ""
            }
        }
    }

    private fun setSalary(vacancySalary: VacancyCardSalary?) {
        if (vacancySalary != null) {
            if ((vacancySalary.to != null) or (vacancySalary.from != null)) {
                var salary = ""
                if (vacancySalary.from != null) {
                    salary += itemView.context.getString(R.string.from) + " "
                    salary += formatNumber(vacancySalary.from) + " "
                }
                if (vacancySalary.to != null) {
                    salary += itemView.context.getString(R.string.to) + " "
                    salary += formatNumber(vacancySalary.to) + " "
                }
                val currency = formatCurrency(vacancySalary.currency)
                val result = salary + currency
                binding.salary.text = result
            } else {
                binding.salary.setText(R.string.salary_not_set)
            }
        } else {
            binding.salary.setText(R.string.salary_not_set)
        }
    }

    private fun setLogo(logo: String?) {
        if (logo != null) {
            binding.imageRecyclerView.load(logo) {
                setHeader(
                    "User-Agent",
                    "practicum-android-diploma/1.0 (https://github.com/sashaqwert/practicum-android-diploma)"
                )

                if (logo.endsWith(".svg", ignoreCase = true)) {
                    decoderFactory(SvgDecoder.Factory())
                }

                scale(Scale.FIT)

                placeholder(R.drawable.img_empty_logo_48_xml)
                error(R.drawable.img_empty_logo_48_xml)

                listener(
                    onError = { _, result ->
                        Log.e("CoilError", "Failed to load: $logo", result.throwable)
                    }
                )
            }
        } else {
            binding.imageRecyclerView.setImageResource(R.drawable.img_empty_logo_48_xml)
        }
    }

    companion object {
        fun from(parent: ViewGroup): VacancyViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemVacancyBinding.inflate(inflater, parent, false)
            return VacancyViewHolder(binding)
        }
        private const val CHUNKED = 3
    }
}
