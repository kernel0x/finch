package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.special.Header
import com.kernel.finch.core.list.cells.HeaderCell

internal class HeaderDelegate : Component.Delegate<Header> {

    override fun createCells(component: Header): List<Cell<*>> = listOf(
        HeaderCell(
            id = component.id,
            title = component.title,
            subtitle = component.subtitle,
            text = component.text
        )
    )
}
