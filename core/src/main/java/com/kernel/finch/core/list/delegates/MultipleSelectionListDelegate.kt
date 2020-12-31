package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.MultipleSelectionList
import com.kernel.finch.core.list.cells.ExpandedItemCheckBoxCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate
import com.kernel.finch.core.list.delegates.shared.ValueWrapperComponentDelegate

internal class MultipleSelectionListDelegate<T : FinchListItem> :
    ExpandableComponentDelegate<MultipleSelectionList<T>>,
    ValueWrapperComponentDelegate.StringSet<MultipleSelectionList<T>>() {

    override fun canExpand(component: MultipleSelectionList<T>) = component.items.isNotEmpty()

    override fun getTitle(component: MultipleSelectionList<T>) =
        super.getTitle(component).let { text ->
            if (component.shouldRequireConfirmation && hasPendingChanges(component)) text.with("*") else text
        }

    override fun MutableList<Cell<*>>.addItems(component: MultipleSelectionList<T>) {
        addAll(component.items.map { item ->
            ExpandedItemCheckBoxCell(
                id = "${component.id}_${item.id}",
                text = item.title,
                isChecked = getUiValue(component).contains(item.id),
                isEnabled = component.isEnabled,
                onValueChanged = { isChecked ->
                    if (isChecked) {
                        setUiValue(component, getUiValue(component) + item.id)
                    } else {
                        setUiValue(component, getUiValue(component) - item.id)
                    }
                }
            )
        })
    }

    override fun createCells(component: MultipleSelectionList<T>) =
        super.createCells(component).also {
            callListenerForTheFirstTimeIfNeeded(component, getCurrentValue(component))
        }
}
