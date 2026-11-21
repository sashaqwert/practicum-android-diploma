package ru.practicum.android.diploma.filter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.databinding.ItemAreaBinding

class AreaAdapter(
    private val onAreaClick: (FilterArea) -> Unit
) : RecyclerView.Adapter<AreaViewHolder>() {
    private var areas = emptyList<FilterArea>()

    fun submitAreas(areas: List<FilterArea>) {
        this.areas = areas.toList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val binding = ItemAreaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AreaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        holder.bind(areas[position], onAreaClick)
    }

    override fun getItemCount(): Int = areas.size
}
