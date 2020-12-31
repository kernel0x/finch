package com.kernel.finch.components.special

import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text
import java.text.SimpleDateFormat
import java.util.*

data class Logs(
    val title: Text = Text.CharSequence(DEFAULT_TITLE),
    val maxItemCount: Int = DEFAULT_MAX_ITEM_COUNT,
    val timeFormatter: ((Long) -> CharSequence) = { DEFAULT_DATE_FORMAT.format(it) },
    val label: String? = DEFAULT_LABEL,
    val isHorizontalScrollEnabled: Boolean = DEFAULT_IS_HORIZONTAL_SCROLL_ENABLED,
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY
) : ExpandableComponent<Logs> {

    override val id = formatId(label)

    override fun getHeaderTitle(finch: Finch) = title

    companion object {
        private const val DEFAULT_TITLE = "Logs"
        private const val DEFAULT_MAX_ITEM_COUNT = 10
        private val DEFAULT_DATE_FORMAT by lazy {
            SimpleDateFormat(
                Finch.LOG_TIME_FORMAT,
                Locale.ENGLISH
            )
        }
        private val DEFAULT_LABEL: String? = null
        private const val DEFAULT_IS_HORIZONTAL_SCROLL_ENABLED = false
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false

        fun formatId(label: String?) = "logList_$label"
    }
}
