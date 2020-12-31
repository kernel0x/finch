package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.Switch
import com.kernel.finch.core.list.cells.SwitchCell
import com.kernel.finch.core.list.delegates.shared.ValueWrapperComponentDelegate

internal class SwitchDelegate : ValueWrapperComponentDelegate.Boolean<Switch>() {

    override fun createCells(component: Switch): List<Cell<*>> =
        getUiValue(component).let { uiValue ->
            listOf(
                SwitchCell(
                    id = component.id,
                    text = component.text(uiValue).let { text ->
                        if (component.shouldRequireConfirmation && hasPendingChanges(component))
                            text.with("*")
                        else
                            text
                    },
                    isChecked = uiValue,
                    isEnabled = component.isEnabled,
                    onValueChanged = { newValue -> setUiValue(component, newValue) }
                )
            )
        }
}
