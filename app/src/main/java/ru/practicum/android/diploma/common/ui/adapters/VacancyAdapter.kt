package ru.practicum.android.diploma.common.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.VacancyCard

class VacancyAdapter (private val vacancyes: MutableList<VacancyCard>, val callback: OnVacancyClickCallback) : RecyclerView.Adapter<VacancyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VacancyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vacancy, parent, false)
        return VacancyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: VacancyViewHolder,
        position: Int
    ) {
        holder.bind(vacancyes[position], callback)
    }

    override fun getItemCount(): Int {
        return vacancyes.size
    }
}
