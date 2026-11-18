package ru.practicum.android.diploma.filter.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.databinding.ItemIndustryBinding

class IndustryViewHolder(private val binding: ItemIndustryBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(industry: FilterIndustry, isSelected: Boolean, onIndustryClick: (FilterIndustry) -> Unit) {
        with(binding.root) {
            text = industry.name
            isChecked = isSelected
            setOnClickListener { onIndustryClick(industry) }
        }
    }
}
