package com.kernel.finch.common.models

import androidx.annotation.StyleRes
import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.loggers.FinchLogger
import com.kernel.finch.common.loggers.FinchNetworkLogger
import java.text.SimpleDateFormat
import java.util.*

data class Configuration(
    @StyleRes val themeResourceId: Int? = DEFAULT_THEME_RESOURCE_ID,
    val shakeThreshold: Int? = DEFAULT_SHAKE_THRESHOLD,
    val shakeHapticFeedbackDuration: Long = DEFAULT_HAPTIC_FEEDBACK_DURATION,
    val excludedPackageNames: List<String> = DEFAULT_EXCLUDED_PACKAGE_NAMES,
    val logger: FinchLogger? = DEFAULT_LOGGER,
    val networkLoggers: List<FinchNetworkLogger> = DEFAULT_NETWORK_LOGGERS,
    val showNotificationNetworkLoggers: Boolean = DEFAULT_SHOW_NOTIFICATION_NETWORK_LOGGERS,
    val logFileNameFormatter: (Long) -> CharSequence = {
        "log_${DEFAULT_FILE_NAME_FORMAT.format(it)}"
    },
    val networkLogFileNameFormatter: (Long) -> CharSequence = {
        "networklog_${DEFAULT_FILE_NAME_FORMAT.format(it)}"
    },
    val imageFileNameFormatter: (Long) -> CharSequence = {
        "image_${DEFAULT_FILE_NAME_FORMAT.format(it)}"
    },
    val videoFileNameFormatter: (Long) -> CharSequence = {
        "video_${DEFAULT_FILE_NAME_FORMAT.format(it)}"
    },
    val galleryDateFormatter: ((Long) -> CharSequence) = { DEFAULT_GALLERY_DATE_FORMAT.format(it) },
    val applyInsets: ((windowInsets: Inset) -> Inset)? = DEFAULT_APPLY_INSETS
) {
    companion object {
        private const val DEFAULT_SHAKE_THRESHOLD = 13
        private const val DEFAULT_SHOW_NOTIFICATION_NETWORK_LOGGERS = true
        private const val DEFAULT_HAPTIC_FEEDBACK_DURATION = 100L
        private val DEFAULT_THEME_RESOURCE_ID: Int? = null
        private val DEFAULT_EXCLUDED_PACKAGE_NAMES = emptyList<String>()
        private val DEFAULT_LOGGER: FinchLogger? = null
        private val DEFAULT_NETWORK_LOGGERS = emptyList<FinchNetworkLogger>()
        private val DEFAULT_FILE_NAME_FORMAT by lazy {
            SimpleDateFormat(
                Finch.FILE_NAME_DATE_TIME_FORMAT,
                Locale.ENGLISH
            )
        }
        private val DEFAULT_GALLERY_DATE_FORMAT by lazy {
            SimpleDateFormat(
                Finch.GALLERY_DATE_FORMAT,
                Locale.ENGLISH
            )
        }
        private val DEFAULT_APPLY_INSETS: ((windowInsets: Inset) -> Inset)? = null
    }
}
