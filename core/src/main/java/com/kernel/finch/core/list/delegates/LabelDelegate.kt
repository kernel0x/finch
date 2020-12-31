package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.Label
import com.kernel.finch.core.util.createTextComponentFromType

internal class LabelDelegate : Component.Delegate<Label> {

    override fun createCells(component: Label): List<Cell<*>> = listOf(
        createTextComponentFromType(
            type = component.type,
            id = component.id,
            text = component.text,
            isEnabled = component.isEnabled,
            icon = component.icon,
            onItemSelected = component.onItemSelected
        )
    )
}
