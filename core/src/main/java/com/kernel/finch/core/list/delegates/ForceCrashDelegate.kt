package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.special.ForceCrash
import com.kernel.finch.core.util.createTextComponentFromType

internal class ForceCrashDelegate : Component.Delegate<ForceCrash> {

    override fun createCells(component: ForceCrash): List<Cell<*>> = listOf(
        createTextComponentFromType(
            type = component.type,
            id = component.id,
            text = component.text,
            isEnabled = component.isEnabled,
            icon = component.icon,
            onItemSelected = {
                component.onButtonPressed()
                throw RuntimeException(component.exception)
            }
        )
    )
}
