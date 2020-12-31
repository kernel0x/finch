package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.KeyValueList
import com.kernel.finch.core.list.cells.ExpandedItemKeyValueCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate

internal class KeyValueListDelegate : ExpandableComponentDelegate<KeyValueList> {

    override fun canExpand(component: KeyValueList) = component.pairs.isNotEmpty()

    override fun MutableList<Cell<*>>.addItems(component: KeyValueList) {
        addAll(component.pairs.mapIndexed { index, item ->
            ExpandedItemKeyValueCell(
                id = "${component.id}_$index",
                key = item.first,
                value = item.second
            )
        })
    }
}
