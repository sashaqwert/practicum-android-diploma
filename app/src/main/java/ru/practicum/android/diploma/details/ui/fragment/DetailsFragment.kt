package ru.practicum.android.diploma.details.ui.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.LeadingMarginSpan
import android.text.style.LineHeightSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.util.TypedValueCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.presentation.state.DetailsState
import ru.practicum.android.diploma.details.presentation.view_model.DetailsViewModel
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt
import androidx.core.net.toUri
import com.bumptech.glide.RequestBuilder
import ru.practicum.android.diploma.details.ui.util.prepareLogoForGlide

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

        viewModel.setID("000108c1-f7f5-3241-b9a2-44f6f1a2e58f") // vacancyId ?: "0"
        viewModel.fillData()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
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

    private fun setContent(vacancyDetail: VacancyDetail) {
        val textSalary = vacancyDetail.salary.let { salary ->
            val salaryFrom = if (salary?.from != null) getString(R.string.salary_from, salary.from) else ""
            val salaryTo = if (salary?.to != null) getString(R.string.salary_to, salary.to) else ""

            "$salaryFrom$salaryTo"
        }

        val employmentText = if (vacancyDetail.employment == null) "" else "${vacancyDetail.employment.name}, "
        val scheduleText = if (vacancyDetail.schedule == null) "" else vacancyDetail.schedule.name

        bindItems(vacancyDetail, textSalary, employmentText, scheduleText)
    }

    private fun bindItems(
        vacancyDetail: VacancyDetail,
        textSalary: String,
        employmentText: String,
        scheduleText: String
    ) {
        lifecycleScope.launch {
            val glide = Glide.with(this@DetailsFragment)
            val logo = when (val logo = prepareLogoForGlide(vacancyDetail.employer.logo)) {
                is Bitmap -> glide.load(logo)
                else -> glide.load(logo as? String)
            }

            logo
                .placeholder(R.drawable.img_empty_logo_48_xml)
                .error(R.drawable.img_empty_logo_48_xml)
                .transform(
                    RoundedCorners(
                        TypedValueCompat.dpToPx(R.dimen.margin_12.toFloat(), resources.displayMetrics).toInt()
                    )
                )
                .into(viewBinding.employerIcon)
        }
        viewBinding.apply {
            vacancyName.setText(vacancyDetail.name)
            salary.text =
                if (textSalary.isNotEmpty() && textSalary.isNotBlank()) textSalary else getString(R.string.salary_not_set)

            employerName.text = vacancyDetail.employer.name
            employerLocation.text = vacancyDetail.area.name
            experienceValue.text =
                if (vacancyDetail.experience == null) getString(R.string.experience_not_set) else vacancyDetail.experience.name
            conditions.text = "$employmentText$scheduleText"
            description.setLineSpacing(0f, 1f)
            description.text = bindDescription(vacancyDetail.description)
            skillsList.text = bindSkills(vacancyDetail)
            likeButton.setImageResource(
                if (vacancyDetail.isFavorite) R.drawable.ic_like_pressed
                else R.drawable.ic_like
            )
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

    private fun bindSkills(vacancy: VacancyDetail): SpannableStringBuilder {
        val skills = vacancy.skills
        val gap = resources.getDimensionPixelSize(R.dimen.margin_4)

        viewBinding.apply {
            skillsTitle.isVisible = skills.isNotEmpty()
            skillsList.isVisible = skills.isNotEmpty()
        }

        return SpannableStringBuilder().apply {
            skills.forEachIndexed { index, skill ->
                if (index > 0) append('\n')

                val start = length
                append(skill)
                setSpan(BulletSpan(gap), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun bindDescription(html: String): SpannableString {
        val prepared = "<body>$html</body>"
            .replace("<h2>", "<div><vacancy-h2>")
            .replace("</h2>", "</vacancy-h2></div>")
            .replace("<h3>", "<div><vacancy-h3>")
            .replace("</h3>", "</vacancy-h3></div>")
            .replace("<p>", "<div><vacancy-p>")
            .replace("</p>", "</vacancy-p></div>")

        val sectionStarts = mutableListOf<Int>()
        var headingStart = 0

        val headingGapPx = resources.getDimensionPixelSize(R.dimen.margin_4)
        val headingEnds = mutableListOf<Int>()

        val h2Ranges = mutableListOf<IntRange>()

        val parsed = prepared.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT, null) { opening, tag, output, _ ->
            val style = when (tag) {
                "vacancy-h2" -> R.style.Text_Medium22
                "vacancy-h3" -> R.style.Text_Medium16
                "vacancy-p" -> R.style.Text_Regular16
                else -> return@parseAsHtml
            }

            if (opening) {
                headingStart = output.length
            } else if (headingStart < output.length) {
                output.setSpan(
                    TextAppearanceSpan(context, style),
                    headingStart,
                    output.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                if (tag == "vacancy-h2" || tag == "vacancy-h3") {
                    headingEnds.add(output.length)
                }

                if (tag == "vacancy-h2") {
                    h2Ranges.add(headingStart until output.length)
                }

                if (tag == "vacancy-h3") {
                    sectionStarts.add(headingStart)
                }
            }
        }

        val gapPx = resources.getDimensionPixelSize(R.dimen.margin_16)
        val formatted = SpannableString(parsed)

        val titleHeight = resources.getDimensionPixelSize(R.dimen.text_26)
        val bodyHeight = resources.getDimensionPixelSize(R.dimen.text_19)

        if (formatted.isNotEmpty()) {
            val lineHeight = LineHeightSpan { _, lineStart, _, _, _, metrics ->
                val height = if (h2Ranges.any { lineStart in it }) titleHeight else bodyHeight
                val extra = height - (metrics.descent - metrics.ascent)
                metrics.ascent -= extra / 2
                metrics.descent = metrics.ascent + height
            }

            formatted.setSpan(lineHeight, 0, formatted.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        for (headingEnd in headingEnds) {
            val spacing = LineHeightSpan { _, lineStart, lineEnd, _, _, metrics ->
                if (lineStart < headingEnd && lineEnd >= headingEnd) {
                    metrics.descent += headingGapPx
                }
            }
            formatted.setSpan(spacing, headingEnd - 1, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val bulletGapPx = resources.getDimensionPixelSize(R.dimen.margin_4)
        val bullets = formatted.getSpans(0, formatted.length, BulletSpan::class.java)
        val bulletIndentPx = resources.getDimensionPixelSize(R.dimen.margin_8)

        for (bullet in bullets) {
            val start = formatted.getSpanStart(bullet)
            val end = formatted.getSpanEnd(bullet)
            val flags = formatted.getSpanFlags(bullet)

            formatted.removeSpan(bullet)
            formatted.setSpan(LeadingMarginSpan.Standard(bulletIndentPx), start, end, flags)
            formatted.setSpan(BulletSpan(bulletGapPx), start, end, flags)
        }

        for (start in sectionStarts) {
            val gap = LineHeightSpan { _, lineStart, _, _, _, metrics ->
                if (lineStart == start) {
                    metrics.ascent -= gapPx
                }
            }

            formatted.setSpan(gap, start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return SpannableString.valueOf(formatted.trimEnd('\n'))
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
