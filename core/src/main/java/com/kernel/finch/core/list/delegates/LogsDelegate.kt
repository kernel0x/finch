package com.kernel.finch.core.list.delegates

import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.special.Logs
import com.kernel.finch.core.list.cells.ExpandedItemTextCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate

internal class LogsDelegate : ExpandableComponentDelegate<Logs> {

    override fun canExpand(component: Logs) =
        FinchCore.implementation.getLogEntries(component.label).isNotEmpty()

    override fun MutableList<Cell<*>>.addItems(component: Logs) {
        addAll(
            FinchCore.implementation.getLogEntries(component.label).take(component.maxItemCount)
                .map { entry ->
                    ExpandedItemTextCell(
                        id = "${component.id}_${entry.id}",
                        text = Text.CharSequence(
                            entry.title.charSequence.toString() + "\n" +
                                component.timeFormatter(entry.date)
                        ),
                        isEnabled = true,
                        onItemSelected = if (entry.payload != null) fun() {
                            FinchCore.implementation.showDialog(
                                contents = entry.payload,
                                timestamp = entry.date,
                                isHorizontalScrollEnabled = component.isHorizontalScrollEnabled
                            )
                        } else null
                    )
                })
    }
}
