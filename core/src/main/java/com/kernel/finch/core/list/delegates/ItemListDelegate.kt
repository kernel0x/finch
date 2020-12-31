package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.ItemList
import com.kernel.finch.core.list.cells.ExpandedItemTextCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate

internal class ItemListDelegate<T : FinchListItem> : ExpandableComponentDelegate<ItemList<T>> {

    override fun canExpand(component: ItemList<T>) = component.items.isNotEmpty()

    override fun MutableList<Cell<*>>.addItems(component: ItemList<T>) {
        addAll(component.items.map { item ->
            ExpandedItemTextCell(
                id = "${component.id}_${item.id}",
                text = item.title,
                isEnabled = true,
                onItemSelected = component.onItemSelected?.let { onItemSelected ->
                    {
                        component.items.firstOrNull { it.id == item.id }?.let { onItemSelected(it) }
                        Unit
                    }
                }
            )
        })
    }
}
