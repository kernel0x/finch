package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.CheckBox
import com.kernel.finch.core.list.cells.CheckBoxCell
import com.kernel.finch.core.list.delegates.shared.ValueWrapperComponentDelegate

internal class CheckBoxDelegate : ValueWrapperComponentDelegate.Boolean<CheckBox>() {

    override fun createCells(component: CheckBox): List<Cell<*>> =
        getUiValue(component).let { uiValue ->
            listOf(
                CheckBoxCell(
                    id = component.id,
                    text = component.text(uiValue).let { title ->
                        if (component.shouldRequireConfirmation && hasPendingChanges(component))
                            title.with("*")
                        else
                            title
                    },
                    isChecked = uiValue,
                    isEnabled = component.isEnabled,
                    onValueChanged = { setUiValue(component, it) })
            )
        }
}
