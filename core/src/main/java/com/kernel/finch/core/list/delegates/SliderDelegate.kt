package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.Slider
import com.kernel.finch.core.list.cells.SliderCell
import com.kernel.finch.core.list.delegates.shared.ValueWrapperComponentDelegate

internal class SliderDelegate : ValueWrapperComponentDelegate.Integer<Slider>() {

    override fun createCells(component: Slider): List<Cell<*>> =
        getUiValue(component).let { uiValue ->
            listOf(
                SliderCell(
                    id = component.id,
                    text = component.text(uiValue).let { text ->
                        if (component.shouldRequireConfirmation && hasPendingChanges(component))
                            text.with("*")
                        else
                            text
                    },
                    value = uiValue,
                    minimumValue = component.minimumValue,
                    maximumValue = component.maximumValue,
                    isEnabled = component.isEnabled,
                    onValueChanged = { setUiValue(component, it) }
                )
            )
        }
}
