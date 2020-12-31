package com.kernel.finch.core.list.delegates

import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.special.LifecycleLogs
import com.kernel.finch.core.list.cells.ExpandedItemTextCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate

internal class LifecycleLogsDelegate : ExpandableComponentDelegate<LifecycleLogs> {

    override fun canExpand(component: LifecycleLogs) =
        FinchCore.implementation.getLifecycleLogEntries(component.eventTypes).isNotEmpty()

    override fun MutableList<Cell<*>>.addItems(component: LifecycleLogs) {
        addAll(
            FinchCore.implementation.getLifecycleLogEntries(component.eventTypes)
                .take(component.maxItemCount).map { entry ->
                    ExpandedItemTextCell(
                        id = "${component.id}_${entry.id}",
                        text = Text.CharSequence(
                            entry.getFormattedTitle(component.shouldDisplayFullNames) + "\n" +
                                component.timeFormatter(entry.date)
                        ),
                        isEnabled = true,
                        onItemSelected = null
                    )
                })
    }
}
