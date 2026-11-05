package ru.practicum.android.diploma.common.ui.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.VacancyCard

class VacancyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    private val vacation_name : TextView = itemView.findViewById<TextView>(R.id.vacation_name)
    private val industry : TextView = itemView.findViewById<TextView>(R.id.industry)
    private val salary : TextView = itemView.findViewById<TextView>(R.id.salary)
    private val image_recycler_view : ImageView = itemView.findViewById<ImageView>(R.id.image_recycler_view)

    fun bind(vacancy: VacancyCard, callback: OnVacancyClickCallback) {
        vacation_name.setText(vacancy.name)
        if (vacancy.company != null) {
            industry.setText(vacancy.company)
        } else {
            industry.setText("")
        }
        if (vacancy.salary != null) {
            var s = ""
            if (vacancy.salary.from != null) {
                s += itemView.context.getString(R.string.from) + " "
                s += vacancy.salary.from.toString() + " "
            }
            if (vacancy.salary.to != null) {
                s += itemView.context.getString(R.string.to) + " "
                s += vacancy.salary.to.toString() + " "
            }
            salary.setText(s)
        } else {
            salary.setText(R.string.salary_not_set)
        }
        if (vacancy.logo != null) {
            Glide.with(itemView)
                .load(vacancy.logo)
                .placeholder(R.drawable.img_empty_logo_48_xml)
                .into(image_recycler_view)
        } else {
            image_recycler_view.setImageResource(R.drawable.img_empty_logo_48_xml)
        }
        itemView.setOnClickListener {
            callback.callback(vacancy)
        }
    }
}
