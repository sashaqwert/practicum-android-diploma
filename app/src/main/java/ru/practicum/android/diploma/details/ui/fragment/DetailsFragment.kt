package ru.practicum.android.diploma.details.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.decode.SvgDecoder
import coil.load
import coil.size.Scale
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.presentation.state.DetailsState
import ru.practicum.android.diploma.details.presentation.viewmodel.DetailsViewModel
import ru.practicum.android.diploma.details.ui.util.HTTPResponses
import ru.practicum.android.diploma.details.ui.util.bindDescription
import ru.practicum.android.diploma.details.ui.util.bindListToBullets
import ru.practicum.android.diploma.details.ui.util.getTextSalary
import ru.practicum.android.diploma.details.ui.util.setSpanToText

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

        viewModel.setID(vacancyId ?: "0")
        viewModel.fillData()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: DetailsState) {
        when (state) {
            is DetailsState.Content -> {
                setLayoutVisibilities(
                    isVacancyLayoutVisible = true,
                    isErrorLayoutVisible = false,
                    isLoadingBarVisible = false
                )
                setContent(state.vacancyDetail)
            }

            is DetailsState.NotFound -> {
                viewBinding.apply {
                    errorImg.setImageResource(R.drawable.img_not_found)
                    errorText.setText(R.string.vacancy_not_found)
                }
            }

            is DetailsState.OtherError -> {
                setLayoutVisibilities(
                    isVacancyLayoutVisible = false,
                    isErrorLayoutVisible = true,
                    isLoadingBarVisible = false
                )

                setError(state.code)
            }

            DetailsState.Loading -> {
                setLayoutVisibilities(
                    isVacancyLayoutVisible = false,
                    isErrorLayoutVisible = false,
                    isLoadingBarVisible = true
                )
            }
        }
    }

    private fun setLayoutVisibilities(
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

        val textSalary = getTextSalary(vacancyDetail, resources)

        val employmentText =
            if (vacancyDetail.employment == null) {
                ""
            } else {
                "${vacancyDetail.employment.name}, "
            }
        val scheduleText =
            if (vacancyDetail.schedule == null) {
                ""
            } else {
                vacancyDetail.schedule.name
            }

        bindItems(vacancyDetail, textSalary, employmentText, scheduleText)

        setListeners(vacancyDetail)
    }

    private fun bindItems(
        vacancyDetail: VacancyDetail,
        textSalary: String,
        employmentText: String,
        scheduleText: String
    ) {
        setCoil(vacancyDetail)

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
            val text = "$employmentText$scheduleText"
            conditions.text = text
            description.setLineSpacing(0f, 1f)
            description.text = bindDescription(vacancyDetail.description, resources, context)
            skillsList.text = bindListToBullets(vacancyDetail.skills, resources)
            likeButton.setImageResource(
                if (vacancyDetail.isFavorite) {
                    R.drawable.ic_like_pressed
                } else {
                    R.drawable.ic_like
                }
            )

            skillsTitle.isVisible = vacancyDetail.skills.isNotEmpty()
            skillsList.isVisible = vacancyDetail.skills.isNotEmpty()

            setContacts(vacancyDetail)
        }
    }

    private fun setCoil(vacancyDetail: VacancyDetail) {
        viewBinding.employerIcon.load(vacancyDetail.employer.logo) {
            setHeader(
                "User-Agent",
                "practicum-android-diploma/1.0 (https://github.com/sashaqwert/practicum-android-diploma)"
            )

            if (vacancyDetail.employer.logo.endsWith(".svg", ignoreCase = true)) {
                decoderFactory(SvgDecoder.Factory())
            }

            scale(Scale.FIT)

            placeholder(R.drawable.img_empty_logo_48_xml)
            error(R.drawable.img_empty_logo_48_xml)

            listener(
                onError = { _, result ->
                    Log.e("CoilError", "Failed to load: ${vacancyDetail.employer.logo}", result.throwable)
                }
            )
        }
    }

    private fun setError(code: Int) {
        when (code) {
            HTTPResponses.NO_DETAIL_RESPONSE -> {
                viewBinding.apply {
                    errorImg.setImageResource(R.drawable.img_not_found)
                    errorText.setText(R.string.vacancy_not_found)
                }
            }

            HTTPResponses.UNKNOWN_ERROR -> {
                viewBinding.apply {
                    errorImg.setImageResource(R.drawable.img_server_error)
                    errorText.setText(R.string.server_error)
                }
            }

            HTTPResponses.NO_INTERNET -> {
                viewBinding.apply {
                    errorImg.setImageResource(R.drawable.img_not_internet_search_state_328)
                    errorText.setText(R.string.no_internet)
                }
            }
        }
    }

    private fun setContacts(vacancyDetail: VacancyDetail) {
        vacancyDetail.contacts?.let { contacts ->
            val phones = contacts.phones.map {
                it.formatted
            }

            val blueColor = ContextCompat.getColor(requireContext(), R.color.blue)

            viewBinding.apply {
                contactsMail.isVisible = checkStringOnEmpty(contacts.email)
                contactsNumbers.isVisible = contacts.phones.isNotEmpty()


                contactsName.text = setSpanToText(contacts.name, resources)
                contactsMail.text = setSpanToText(contacts.email, resources) {
                    blueColor
                }

                contactsNumbers.text = bindListToBullets(
                    phones,
                    resources,
                    onItemClick = { phone ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:$phone".toUri()
                        }

                        startActivity(intent)
                    },
                    colorForItem = { blueColor }
                )

                contactsNumbers.movementMethod = LinkMovementMethod.getInstance()
            }
        } ?: {
            viewBinding.apply {
                contactsHeader.visibility = View.GONE
                contactsName.visibility = View.GONE
                contactsMail.visibility = View.GONE
                contactsNumbers.visibility = View.GONE
            }
        }
    }

    private fun checkStringOnEmpty(text: String): Boolean {
        return text.isNotEmpty() && text.isNotBlank()
    }

    private fun setListeners(vacancyDetail: VacancyDetail) {
        viewBinding.apply {
            likeButton.setOnClickListener {
                viewModel.actionFavoriteClicked()
            }

            contactsMail.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)

                intent.data = Uri.fromParts("mailto", vacancyDetail.contacts?.email, null)

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
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
    }
}
