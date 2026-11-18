package ru.practicum.android.diploma.filter.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.databinding.ItemAreaBinding

class AreaViewHolder(private val binding: ItemAreaBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(area: FilterArea, onAreaClick: (FilterArea) -> Unit) {
        with(binding.root) {
            text = area.name
            setOnClickListener { onAreaClick(area) }
        }
    }
}
