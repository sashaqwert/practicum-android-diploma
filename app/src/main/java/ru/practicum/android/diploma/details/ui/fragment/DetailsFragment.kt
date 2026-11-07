package ru.practicum.android.diploma.details.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.TypedValueCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.common.domain.models.VacancyCardSalary
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding
import ru.practicum.android.diploma.details.domain.model.Address
import ru.practicum.android.diploma.details.domain.model.Contacts
import ru.practicum.android.diploma.details.domain.model.Employer
import ru.practicum.android.diploma.details.domain.model.Employment
import ru.practicum.android.diploma.details.domain.model.Experience
import ru.practicum.android.diploma.details.domain.model.Schedule
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.presentation.state.DetailsState
import ru.practicum.android.diploma.details.presentation.view_model.DetailsViewModel

class DetailsFragment : Fragment() {

    private var vacancyId: String? = null
    private var _viewBinding: FragmentDetailsBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel by viewModel<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vacancyId = it.getString(CURRENT_VACANCY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentDetailsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setID(vacancyId ?: "0")

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is DetailsState.Content -> setContent(state.vacancyDetail)
                is DetailsState.Error -> {
                    setLayoutVisibilites(
                        isVacancyLayoutVisible = false,
                        isErrorLayoutVisible = true,
                        isLoadingBarVisible = true
                    )

                    setError(state.code)
                }
                DetailsState.Loading -> {
                    setLayoutVisibilites(
                        isVacancyLayoutVisible = false,
                        isErrorLayoutVisible = false,
                        isLoadingBarVisible = true
                    )
                }
            }
        }
    }

    private fun setLayoutVisibilites(
        isVacancyLayoutVisible: Boolean,
        isErrorLayoutVisible: Boolean,
        isLoadingBarVisible: Boolean
    ) {
        viewBinding.apply {
            vacancyLayout.visibility = if (isVacancyLayoutVisible) View.VISIBLE else View.GONE
            errorLayout.visibility = if (isErrorLayoutVisible) View.VISIBLE else View.GONE
            loadingBar.visibility = if (isLoadingBarVisible) View.VISIBLE else View.GONE
        }
    }
    //    val address: Address?,
//    val area: FilterArea,
//    val contacts: Contacts?,
//    val description: String,
//    val employer: Employer,
//    val employment: Employment?,
//    val experience: Experience?,
//    val id: String,
//    val industry: FilterIndustry,
//    val name: String,
//    val salary: VacancyCardSalary?,
//    val schedule: Schedule?,
//    val skills: List<String>,
//    val url: String,
//    val isFavorite: Boolean = false
    private fun setContent(vacancyDetail: VacancyDetail) {
        val textSalary = vacancyDetail.salary.let { salary ->
            val salaryFrom = salary?.from.let { getString(R.string.salary_from, it) }
            val salaryTo = salary?.to.let { getString(R.string.salary_to, it) }

            "$salaryFrom$salaryTo"
        }

        val employmentText = if (vacancyDetail.employment == null) "" else "${vacancyDetail.employment.name}, "
        val scheduleText = if (vacancyDetail.schedule == null) "" else vacancyDetail.schedule.name

        viewBinding.apply {
            vacancyName.setText(vacancyDetail.name)
            salary.text =
                if (textSalary.isNotEmpty() && textSalary.isNotBlank()) textSalary else getString(R.string.salary_not_set)

            Glide
                .with(this@DetailsFragment)
                .load(vacancyDetail.employer.logo)
                .placeholder(R.drawable.img_empty_logo_48_xml)
                .error(R.drawable.img_empty_logo_48_xml)
                .transform(
                    RoundedCorners(
                        TypedValueCompat.dpToPx(12f, resources.displayMetrics).toInt()
                    )
                )
                .into(employerIcon)

            employerName.text = vacancyDetail.employer.name
            employerLocation.text = vacancyDetail.area.name
            experienceValue.text = if (vacancyDetail.experience == null) getString(R.string.experience_not_set) else vacancyDetail.experience.name
            conditions.text = "$employmentText$scheduleText"
        }
    }

    private fun setError(code: Int) {
        when (code) {
            NO_DETAIL_RESPONSE, NOT_FOUND -> {
                viewBinding.apply {
                    errorImg.setImageResource(R.drawable.img_not_found)
                    errorText.setText(R.string.vacancy_not_found)
                }
            }
            UNKNOWN_ERROR -> {
                viewBinding.apply {
                    errorImg.setImageResource(R.drawable.img_server_error)
                    errorText.setText(R.string.server_error)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        private const val CURRENT_VACANCY_ID = "CURRENT_VACANCY_ID"

        fun putId(id: String): Bundle {
            return Bundle().apply {
                putString(CURRENT_VACANCY_ID, id)
            }
        }

        private const val NO_DETAIL_RESPONSE = -2
        private const val NOT_FOUND = 404
        private const val UNKNOWN_ERROR = -3
    }
}
