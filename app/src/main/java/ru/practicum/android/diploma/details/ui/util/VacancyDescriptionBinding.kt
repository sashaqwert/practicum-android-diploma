package ru.practicum.android.diploma.details.ui.util

import android.content.Context
import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.LeadingMarginSpan
import android.text.style.LineHeightSpan
import android.text.style.TextAppearanceSpan
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.details.domain.model.VacancyDetail

fun bindSkills(vacancy: VacancyDetail, resources: Resources): SpannableStringBuilder {
    val skills = vacancy.skills
    val gap = resources.getDimensionPixelSize(R.dimen.margin_4)

    return SpannableStringBuilder().apply {
        skills.forEachIndexed { index, skill ->
            if (index > 0) append('\n')

            val start = length
            append(skill)
            setSpan(BulletSpan(gap), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

fun bindDescription(html: String, resources: Resources, context: Context?): SpannableString {
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
