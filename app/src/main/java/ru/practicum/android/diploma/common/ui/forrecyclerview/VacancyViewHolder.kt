package ru.practicum.android.diploma.common.ui.forrecyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.TypedValueCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
            var salary = ""
            if (vacancy.salary.from != null) {
                salary += itemView.context.getString(R.string.from) + " "
                salary += vacancy.salary.from.toString() + " "
            }
            if (vacancy.salary.to != null) {
                salary += itemView.context.getString(R.string.to) + " "
                salary += vacancy.salary.to.toString()
            }
            binding.salary.text = salary
        } else {
            binding.salary.setText(R.string.salary_not_set)
        }
        if (vacancy.logo != null) {
            Glide.with(itemView)
                .load(vacancy.logo)
                .placeholder(R.drawable.img_empty_logo_48_xml)
                .error(R.drawable.img_empty_logo_48_xml)
                .transform(
                    RoundedCorners(
                        TypedValueCompat.dpToPx(CORNER_RADIUS_DP, itemView.resources.displayMetrics).toInt()
                    )
                )
                .into(binding.imageRecyclerView)
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
        const val CORNER_RADIUS_DP = 12f
    }
}
