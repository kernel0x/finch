package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.Padding
import com.kernel.finch.core.list.cells.PaddingCell

internal class PaddingDelegate : Component.Delegate<Padding> {

    override fun createCells(component: Padding): List<Cell<*>> =
        listOf(PaddingCell(id = component.id))
}
