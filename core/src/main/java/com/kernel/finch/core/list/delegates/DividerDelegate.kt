package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.Divider
import com.kernel.finch.core.list.cells.DividerCell

internal class DividerDelegate : Component.Delegate<Divider> {

    override fun createCells(component: Divider): List<Cell<*>> =
        listOf(DividerCell(id = component.id))
}
