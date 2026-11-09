package ru.practicum.android.diploma.details.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.presentation.state.DetailsState
import ru.practicum.android.diploma.details.presentation.viewmodel.DetailsViewModel
import ru.practicum.android.diploma.details.ui.util.bindDescription
import ru.practicum.android.diploma.details.ui.util.bindSkills
import ru.practicum.android.diploma.details.ui.util.getCurrencySymbol
import ru.practicum.android.diploma.details.ui.util.prepareLogoForGlide
import java.text.NumberFormat
import java.util.Locale

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

        (requireActivity() as AppCompatActivity).setSupportActionBar(viewBinding.toolBar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        viewBinding.toolBar.setNavigationOnClickListener { findNavController().popBackStack() }

        viewBinding.apply {
            likeButton.setOnClickListener {
                viewModel.actionFavoriteClicked()
            }
        }

        viewModel.setID(vacancyId ?: "0")
        viewModel.fillData()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: DetailsState) {
        when (state) {
            is DetailsState.Content -> {
                setLayoutVisibilites(
                    isVacancyLayoutVisible = true,
                    isErrorLayoutVisible = false,
                    isLoadingBarVisible = false
                )
                setContent(state.vacancyDetail)
            }

            is DetailsState.Error -> {
                setLayoutVisibilites(
                    isVacancyLayoutVisible = false,
                    isErrorLayoutVisible = true,
                    isLoadingBarVisible = false
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

    private fun setContent(vacancyDetail: VacancyDetail) {
        viewBinding.shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, vacancyDetail.url)
            }

            startActivity(intent)
        }

        val textSalary = getTextSalary(vacancyDetail)

        val employmentText =
            if (vacancyDetail.employment == null) {
                ""
            }
            else {
                "${vacancyDetail.employment.name}, "
            }
        val scheduleText =
            if (vacancyDetail.schedule == null) {
                ""
            }
            else {
                vacancyDetail.schedule.name
            }

        bindItems(vacancyDetail, textSalary, employmentText, scheduleText)
    }

    private fun getTextSalary(vacancyDetail: VacancyDetail): String {
        return vacancyDetail.salary?.let { salary ->
            val formatter = NumberFormat.getIntegerInstance(Locale("ru", "RU"))
            val formattedSalaryFrom = salary.from?.let { formatter.format(it) } ?: ""
            val formattedSalaryTo = salary.to?.let { formatter.format(it) } ?: ""

            val currency = getCurrencySymbol(vacancyDetail.salary.currency ?: "")

            val salaryFrom =
                if (salary.from != null) {
                    getString(
                        R.string.salary_from,
                        formattedSalaryFrom,
                        currency
                    )
                } else {
                    ""
                }
            val salaryTo = if (salary.to != null) {
                getString(
                    R.string.salary_to,
                    formattedSalaryTo,
                    currency
                )
            } else {
                ""
            }

            "$salaryFrom$salaryTo"
        } ?: ""
    }

    private fun bindItems(
        vacancyDetail: VacancyDetail,
        textSalary: String,
        employmentText: String,
        scheduleText: String
    ) {
        setGlide(vacancyDetail)

        viewBinding.apply {
            vacancyName.setText(vacancyDetail.name)
            salary.text = if (textSalary.isNotEmpty() && textSalary.isNotBlank()) {
                textSalary
            } else {
                getString(R.string.salary_not_set)
            }

            employerName.text = vacancyDetail.employer.name
            employerLocation.text = vacancyDetail.area.name
            experienceValue.text =
                if (vacancyDetail.experience == null) {
                    getString(R.string.experience_not_set)
                } else {
                    vacancyDetail.experience.name
                }
            conditions.text = "$employmentText$scheduleText"
            description.setLineSpacing(0f, 1f)
            description.text = bindDescription(vacancyDetail.description, resources, context)
            skillsList.text = bindSkills(vacancyDetail, resources)
            likeButton.setImageResource(
                if (vacancyDetail.isFavorite) R.drawable.ic_like_pressed
                else R.drawable.ic_like
            )

            skillsTitle.isVisible = vacancyDetail.skills.isNotEmpty()
            skillsList.isVisible = vacancyDetail.skills.isNotEmpty()
        }
    }

    private fun setGlide(vacancyDetail: VacancyDetail) {
        lifecycleScope.launch {
            viewBinding.employerIcon.setImageResource(R.drawable.img_empty_logo_48_xml)
            val logo = when (val logo = prepareLogoForGlide(vacancyDetail.employer.logo)) {
                is Bitmap -> logo
                else -> logo as? String
            }

            Glide
                .with(this@DetailsFragment)
                .load(logo)
                .placeholder(R.drawable.img_empty_logo_48_xml)
                .error(R.drawable.img_empty_logo_48_xml)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen.margin_12))
                )
                .into(viewBinding.employerIcon)
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

            NO_INTERNET -> {
                viewBinding.apply {
                    errorImg.setImageResource(R.drawable.img_not_internet_search_state_328)
                    errorText.setText(R.string.no_internet)
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
        private const val NO_INTERNET = -1
    }
}
