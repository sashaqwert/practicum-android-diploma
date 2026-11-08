package ru.practicum.android.diploma.details.ui.util

import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

private const val CONNECT_AND_READ_TIMEOUT = 5000
suspend fun prepareLogoForGlide(logoUrl: String): Any? = when {
    logoUrl.isBlank() -> null
    logoUrl.toUri().path?.endsWith(".svg", ignoreCase = true) != true -> {
        logoUrl
    }

    else -> {
        renderSvgLogo(logoUrl)
    }
}

private suspend fun renderSvgLogo(logoUrl: String) {
    withContext(Dispatchers.IO) {
        try {
            val connection = URL(logoUrl).openConnection().apply {
                connectTimeout = CONNECT_AND_READ_TIMEOUT
                readTimeout = CONNECT_AND_READ_TIMEOUT
            }

            SVG.setInternalEntitiesEnabled(false)
            val picture = connection.getInputStream().use {
                SVG.getFromInputStream(it).renderToPicture()
            }

            if (picture.width <= 0 || picture.height <= 0) null

            createBitmap(picture.width, picture.height).apply {
                Canvas(this).drawPicture(picture)
            }
        } catch (_: IOException) {
            null
        } catch (_: SVGParseException) {
            null
        }
    }
}
