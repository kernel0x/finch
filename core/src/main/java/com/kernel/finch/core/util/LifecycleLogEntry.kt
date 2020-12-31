package com.kernel.finch.core.util

import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.special.LifecycleLogs
import java.util.*

internal data class LifecycleLogEntry(
    val classType: Class<*>,
    val eventType: LifecycleLogs.EventType,
    val hasSavedInstanceState: Boolean?,
    val date: Long = System.currentTimeMillis()
) : FinchListItem {

    override val title = Text.CharSequence(UUID.randomUUID().toString())

    fun getFormattedTitle(shouldDisplayFullNames: Boolean) =
        "${(if (shouldDisplayFullNames) classType.name else classType.simpleName)}: ${eventType.formattedName}".let {
            if (hasSavedInstanceState == null) it else "$it, savedInstanceState ${if (hasSavedInstanceState) "!=" else "="} null"
        }
}
