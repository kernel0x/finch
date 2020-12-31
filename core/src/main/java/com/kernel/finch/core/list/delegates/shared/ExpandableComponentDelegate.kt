package com.kernel.finch.core.list.delegates.shared

import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text
import com.kernel.finch.core.list.cells.ExpandableHeaderCell
import com.kernel.finch.core.list.cells.PaddingCell

internal interface ExpandableComponentDelegate<C : ExpandableComponent<C>> : Component.Delegate<C> {

    override fun createCells(component: C): List<Cell<*>> = mutableListOf<Cell<*>>().apply {
        addHeader(component)
        if (component.isExpanded) {
            addItems(component)
            addFooter(component)
        }
    }

    private fun MutableList<Cell<*>>.addHeader(component: C) = add(
        ExpandableHeaderCell(
            id = "header_${component.id}",
            text = getTitle(component),
            isExpanded = component.isExpanded,
            canExpand = canExpand(component),
            onItemSelected = {
                component.isExpanded = !component.isExpanded
                FinchCore.implementation.refresh()
            }
        )
    )


    private fun MutableList<Cell<*>>.addFooter(component: C) =
        add(PaddingCell(id = "footer_${component.id}"))

    fun canExpand(component: C): Boolean

    fun MutableList<Cell<*>>.addItems(component: C)

    fun getTitle(component: C): Text = component.getHeaderTitle(FinchCore.implementation)

    private var ExpandableComponent<C>.isExpanded: Boolean
        get() = FinchCore.implementation.memoryStorageManager.booleans[id] ?: isExpandedInitially
        set(value) {
            FinchCore.implementation.memoryStorageManager.booleans[id] = value
        }
}
