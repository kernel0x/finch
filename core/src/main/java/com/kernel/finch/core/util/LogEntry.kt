package com.kernel.finch.core.util

import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.models.Text

internal data class LogEntry(
    val label: String?,
    val message: CharSequence,
    val payload: CharSequence?,
    val date: Long = System.currentTimeMillis()
) : FinchListItem {

    override val title = Text.CharSequence(message)
    override val id: String = Component.randomId
}
