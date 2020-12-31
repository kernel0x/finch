package com.kernel.finch.core.list.delegates

import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.special.NetworkLogs
import com.kernel.finch.core.list.cells.ExpandedItemTextCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate

internal class NetworkLogsDelegate : ExpandableComponentDelegate<NetworkLogs> {

    override fun canExpand(component: NetworkLogs) =
        FinchCore.implementation.getNetworkLogEntries().isNotEmpty()

    override fun MutableList<Cell<*>>.addItems(component: NetworkLogs) {
        addAll(
            FinchCore.implementation.getNetworkLogEntries().take(component.maxItemCount)
                .map { entry ->
                    val text = when (entry.getStatus()) {
                        NetworkLogEntity.Status.FAIL -> "! ! !"
                        NetworkLogEntity.Status.PROGRESS -> ". . ."
                        else -> entry.responseCode.toString()
                    } + "\n" +
                        entry.method + " " + entry.path + "\n" +
                        entry.host + "\n" +
                        component.timeFormatter.invoke(entry.requestDate)

                    ExpandedItemTextCell(
                        id = "${component.id}_${entry.id}",
                        text = Text.CharSequence(
                            component.maxItemTitleLength?.let { maxItemTitleLength ->
                                if (text.length > maxItemTitleLength) {
                                    "${text.take(maxItemTitleLength)}…"
                                } else text
                            } ?: text
                        ),
                        isEnabled = true,
                        onItemSelected = {
                            FinchCore.implementation.showNetworkEventActivity(
                                entry
                            )
                        }
                    )
                })
    }
}
