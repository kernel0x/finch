package com.kernel.finch.core.util.extension

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.view.ContextThemeWrapper
import androidx.core.content.FileProvider
import com.kernel.finch.FinchCore
import com.kernel.finch.common.models.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Context.applyTheme() =
    FinchCore.implementation.configuration.themeResourceId?.let { ContextThemeWrapper(this, it) }
        ?: this

internal fun Context.getUriForFile(file: File) = FileProvider.getUriForFile(
    applicationContext,
    applicationContext.packageName + ".finch.fileProvider",
    file
)

internal suspend fun Context.createScreenshotFromBitmap(bitmap: Bitmap, fileName: String): Uri? =
    withContext(Dispatchers.IO) {
        val file = createScreenCaptureFile(fileName)
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            getUriForFile(file)
        } catch (_: IOException) {
            null
        }
    }

private const val SCREEN_CAPTURES_FOLDER_NAME = "finchScreenCaptures"

internal fun Context.getScreenCapturesFolder() = getFolder(SCREEN_CAPTURES_FOLDER_NAME)

internal fun Context.createScreenCaptureFile(fileName: String) =
    File(getScreenCapturesFolder(), fileName)

private const val LOGS_FOLDER_NAME = "finchLogs"

internal fun Context.getLogsFolder() = getFolder(LOGS_FOLDER_NAME)

internal fun Context.createLogFile(fileName: String) = File(getLogsFolder(), fileName)

private fun Context.getFolder(name: String): File {
    val folder = File(cacheDir, name)
    folder.mkdirs()
    return folder
}

internal fun Context.text(text: Text) = when (text) {
    is Text.CharSequence -> text.charSequence
    is Text.ResourceId -> getText(text.resId)
}.append(text.suffix)

internal fun Context.text(resId: Int) = getText(resId)
