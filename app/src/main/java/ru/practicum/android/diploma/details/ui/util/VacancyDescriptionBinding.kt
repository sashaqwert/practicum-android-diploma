package ru.practicum.android.diploma.details.ui.util

import android.content.Context
import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.BulletSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.text.style.LineHeightSpan
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import ru.practicum.android.diploma.R

private const val H2_TAG = "vacancy-h2"
private const val H3_TAG = "vacancy-h3"
private const val PARAGRAPH_TAG = "vacancy-p"

fun setSpanToText(
    text: String,
    resources: Resources,
    colorForItem: (String) -> Int? = { null }
): SpannableStringBuilder {
    val gap = resources.getDimensionPixelSize(R.dimen.margin_4)
    return SpannableStringBuilder().apply {
        val start = length
        append(text)
        setSpan(
            BulletSpan(gap),
            start,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        colorForItem(text)?.let { color ->
            setSpan(
                ForegroundColorSpan(color),
                start,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

fun bindListToBullets(
    listToBind: List<String>,
    resources: Resources,
    onItemClick: ((String) -> Unit)? = null,
    colorForItem: (String) -> Int? = { null }
): SpannableStringBuilder {
    val gap = resources.getDimensionPixelSize(R.dimen.margin_4)

    return SpannableStringBuilder().apply {
        listToBind.forEachIndexed { index, el ->
            if (index > 0) append('\n')

            val start = length
            append(el)
            setSpan(BulletSpan(gap), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            colorForItem(el)?.let { color ->
                setSpan(
                    ForegroundColorSpan(color),
                    start,
                    length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            onItemClick?.let { listener ->
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        listener(el)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = false
                    }
                }

                setSpan(
                    clickableSpan,
                    start,
                    length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}

fun bindDescription(html: String, resources: Resources, context: Context?): SpannableString {
    val tagHandler = DescriptionTagHandler(context)
    val parsed = prepareDescriptionHtml(html).parseAsHtml(
        flags = HtmlCompat.FROM_HTML_MODE_COMPACT,
        tagHandler = tagHandler
    )
    val formatted = SpannableString(parsed)

    formatted.applyLineHeights(resources, tagHandler.h2Ranges)
    formatted.addHeadingSpacing(resources, tagHandler.headingEnds)
    formatted.styleBullets(resources)
    formatted.addSectionSpacing(resources, tagHandler.sectionStarts)

    return SpannableString.valueOf(formatted.trimEnd('\n'))
}

private fun prepareDescriptionHtml(html: String): String {
    return "<body>$html</body>"
        .replace("<h2>", "<div><$H2_TAG>")
        .replace("</h2>", "</$H2_TAG></div>")
        .replace("<h3>", "<div><$H3_TAG>")
        .replace("</h3>", "</$H3_TAG></div>")
        .replace("<p>", "<div><$PARAGRAPH_TAG>")
        .replace("</p>", "</$PARAGRAPH_TAG></div>")
}

private fun SpannableString.applyLineHeights(resources: Resources, h2Ranges: List<IntRange>) {
    if (isEmpty()) return

    val titleHeight = resources.getDimensionPixelSize(R.dimen.text_26)
    val bodyHeight = resources.getDimensionPixelSize(R.dimen.text_19)

    val lineHeight = LineHeightSpan { _, lineStart, _, _, _, metrics ->
        val height = if (h2Ranges.any { lineStart in it }) titleHeight else bodyHeight
        val extra = height - (metrics.descent - metrics.ascent)
        metrics.ascent -= extra / 2
        metrics.descent = metrics.ascent + height
    }

    setSpan(lineHeight, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

private fun SpannableString.addHeadingSpacing(resources: Resources, headingEnds: List<Int>) {
    val gapPx = resources.getDimensionPixelSize(R.dimen.margin_4)

    for (headingEnd in headingEnds) {
        val spacing = LineHeightSpan { _, lineStart, lineEnd, _, _, metrics ->
            if (lineStart < headingEnd && lineEnd >= headingEnd) {
                metrics.descent += gapPx
            }
        }
        setSpan(spacing, headingEnd - 1, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

private fun SpannableString.styleBullets(resources: Resources) {
    val bulletGapPx = resources.getDimensionPixelSize(R.dimen.margin_4)
    val bulletIndentPx = resources.getDimensionPixelSize(R.dimen.margin_8)
    val bullets = getSpans(0, length, BulletSpan::class.java)

    for (bullet in bullets) {
        val start = getSpanStart(bullet)
        val end = getSpanEnd(bullet)
        val flags = getSpanFlags(bullet)

        removeSpan(bullet)
        setSpan(LeadingMarginSpan.Standard(bulletIndentPx), start, end, flags)
        setSpan(BulletSpan(bulletGapPx), start, end, flags)
    }
}

private fun SpannableString.addSectionSpacing(resources: Resources, sectionStarts: List<Int>) {
    val gapPx = resources.getDimensionPixelSize(R.dimen.margin_16)

    for (start in sectionStarts) {
        val spacing = LineHeightSpan { _, lineStart, _, _, _, metrics ->
            if (lineStart == start) {
                metrics.ascent -= gapPx
            }
        }
        setSpan(spacing, start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}
