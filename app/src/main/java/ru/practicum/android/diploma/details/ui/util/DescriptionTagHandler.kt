package ru.practicum.android.diploma.details.ui.util

import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import org.xml.sax.XMLReader
import ru.practicum.android.diploma.R

class DescriptionTagHandler(private val context: Context?) : Html.TagHandler {
    val h2Ranges = mutableListOf<IntRange>()
    val headingEnds = mutableListOf<Int>()
    val sectionStarts = mutableListOf<Int>()

    private var tagStart = 0

    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        val style = when (tag) {
            H2_TAG -> R.style.Text_Medium22
            H3_TAG -> R.style.Text_Medium16
            PARAGRAPH_TAG -> R.style.Text_Regular16
            else -> return
        }

        if (opening) {
            tagStart = output.length
        } else if (tagStart < output.length) {
            closeTag(tag, style, output)
        }
    }

    private fun closeTag(tag: String, style: Int, output: Editable) {
        val end = output.length
        output.setSpan(
            TextAppearanceSpan(context, style),
            tagStart,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        when (tag) {
            H2_TAG -> {
                headingEnds.add(end)
                h2Ranges.add(tagStart until end)
            }

            H3_TAG -> {
                headingEnds.add(end)
                sectionStarts.add(tagStart)
            }
        }
    }

    companion object {
        private const val H2_TAG = "vacancy-h2"
        private const val H3_TAG = "vacancy-h3"
        private const val PARAGRAPH_TAG = "vacancy-p"
    }
}
