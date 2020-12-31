package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.ProgressBar
import com.kernel.finch.core.list.cells.ProgressBarCell

internal class ProgressBarDelegate : Component.Delegate<ProgressBar> {

    override fun createCells(component: ProgressBar): List<Cell<*>> =
        listOf(ProgressBarCell(id = component.id))
}
