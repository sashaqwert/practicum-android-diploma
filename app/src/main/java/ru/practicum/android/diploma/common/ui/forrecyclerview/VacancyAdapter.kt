package ru.practicum.android.diploma.common.ui.forrecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.common.domain.models.VacancyCard

class VacancyAdapter(private var vacancies: List<VacancyCard>, private val onItemClick: (VacancyCard) -> Unit) :
    RecyclerView.Adapter<VacancyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VacancyViewHolder {
        return VacancyViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: VacancyViewHolder,
        position: Int
    ) {
        holder.bind(vacancies[position])

        holder.itemView.setOnClickListener {
            onItemClick(vacancies[position])
        }
    }

    override fun getItemCount(): Int {
        return vacancies.size
    }

    fun updateList(newList: List<VacancyCard>) {
        vacancies = newList
        notifyDataSetChanged()
    }
}
