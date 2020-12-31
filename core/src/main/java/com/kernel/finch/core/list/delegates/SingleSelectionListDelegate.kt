package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.SingleSelectionList
import com.kernel.finch.core.list.cells.ExpandedItemRadioButtonCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate
import com.kernel.finch.core.list.delegates.shared.ValueWrapperComponentDelegate

internal class SingleSelectionListDelegate<T : FinchListItem> :
    ExpandableComponentDelegate<SingleSelectionList<T>>,
    ValueWrapperComponentDelegate.String<SingleSelectionList<T>>() {

    override fun canExpand(component: SingleSelectionList<T>) = component.items.isNotEmpty()

    override fun getTitle(component: SingleSelectionList<T>) =
        super.getTitle(component).let { text ->
            if (component.shouldRequireConfirmation && hasPendingChanges(component)) text.with("*") else text
        }

    override fun MutableList<Cell<*>>.addItems(component: SingleSelectionList<T>) {
        addAll(component.items.map { item ->
            ExpandedItemRadioButtonCell(
                id = "${component.id}_${item.id}",
                text = item.title,
                isChecked = item.id == getUiValue(component),
                isEnabled = component.isEnabled,
                onValueChanged = { setUiValue(component, item.id) }
            )
        })
    }

    override fun createCells(component: SingleSelectionList<T>) =
        super.createCells(component).also {
            callListenerForTheFirstTimeIfNeeded(component, getCurrentValue(component))
        }
}
