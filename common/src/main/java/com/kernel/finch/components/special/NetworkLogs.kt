package com.kernel.finch.components.special

import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text
import java.text.SimpleDateFormat
import java.util.*

data class NetworkLogs(
    val title: Text = Text.CharSequence(DEFAULT_TITLE),
    val baseUrl: String = DEFAULT_BASE_URL,
    val maxItemCount: Int = DEFAULT_MAX_ITEM_COUNT,
    val maxItemTitleLength: Int? = DEFAULT_MAX_ITEM_TITLE_LENGTH,
    val timeFormatter: ((Long) -> CharSequence) = { DEFAULT_TIME_FORMAT.format(it) },
    val dateTimeFormatter: ((Long) -> CharSequence) = { DEFAULT_DATETIME_FORMAT.format(it) },
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY
) : ExpandableComponent<NetworkLogs> {

    override val id: String = ID

    override fun getHeaderTitle(finch: Finch) = title

    companion object {
        const val ID = "networkLogs"
        private const val DEFAULT_TITLE = "Network logs"
        private const val DEFAULT_BASE_URL = ""
        private const val DEFAULT_MAX_ITEM_COUNT = 10
        private val DEFAULT_MAX_ITEM_TITLE_LENGTH: Int? = null
        private val DEFAULT_TIME_FORMAT by lazy {
            SimpleDateFormat(
                Finch.LOG_TIME_FORMAT,
                Locale.ENGLISH
            )
        }
        private val DEFAULT_DATETIME_FORMAT by lazy {
            SimpleDateFormat(
                Finch.LOG_DATE_AND_TIME_FORMAT,
                Locale.ENGLISH
            )
        }
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
    }
}
