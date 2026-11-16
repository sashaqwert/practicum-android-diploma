package ru.practicum.android.diploma.filter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.databinding.ItemIndustryBinding

class IndustryAdapter(
    private val onIndustryClick: (FilterIndustry) -> Unit
) : ListAdapter<IndustryAdapter.Item, IndustryViewHolder>(IndustryDiffCallback) {

    fun submitIndustries(industries: List<FilterIndustry>, selectedIndustryId: String?) {
        submitList(industries.map { industry ->
            Item(industry, industry.id == selectedIndustryId)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val binding = ItemIndustryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IndustryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item.industry, item.isSelected, onIndustryClick)
    }

    data class Item(val industry: FilterIndustry, val isSelected: Boolean)

    private object IndustryDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.industry.id == newItem.industry.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem == newItem
    }
}
