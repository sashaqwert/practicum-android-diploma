package ru.practicum.android.diploma.filter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.databinding.ItemIndustryBinding

class IndustryAdapter(
    private val onIndustryClick: (FilterIndustry) -> Unit
) : RecyclerView.Adapter<IndustryViewHolder>() {
    private var allIndustries = emptyList<FilterIndustry>()
    private var visibleIndustries = emptyList<FilterIndustry>()
    private var selectedIndustryId: String? = null
    private var searchQuery = ""

    fun submitIndustries(industries: List<FilterIndustry>, selectedIndustryId: String?) {
        allIndustries = industries.toList()
        this.selectedIndustryId = selectedIndustryId
        updateVisibleIndustries()
    }

    fun filterIndustries(query: String) {
        searchQuery = query.trim()
        updateVisibleIndustries()
    }

    fun getIndustryById(id: String): FilterIndustry? {
        return allIndustries.find { it.id == id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val binding = ItemIndustryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IndustryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val industry = visibleIndustries[position]
        holder.bind(industry, industry.id == selectedIndustryId, onIndustryClick)
    }

    override fun getItemCount(): Int = visibleIndustries.size

    private fun updateVisibleIndustries() {
        visibleIndustries = allIndustries.filter { industry ->
            industry.name.contains(searchQuery, ignoreCase = true)
        }
        notifyDataSetChanged()
    }
}
