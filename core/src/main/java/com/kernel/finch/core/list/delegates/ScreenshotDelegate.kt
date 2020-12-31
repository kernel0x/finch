package com.kernel.finch.core.list.delegates

import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.special.Screenshot
import com.kernel.finch.core.util.createTextComponentFromType
import com.kernel.finch.core.util.performOnHide

internal class ScreenshotDelegate : Component.Delegate<Screenshot> {

    override fun createCells(component: Screenshot): List<Cell<*>> = listOf(
        createTextComponentFromType(
            type = component.type,
            id = component.id,
            text = component.text,
            isEnabled = component.isEnabled,
            icon = component.icon,
            onItemSelected = {
                component.onButtonPressed()
                hideDebugMenuAndTakeScreenshot()
            }
        )
    )

    companion object {
        fun hideDebugMenuAndTakeScreenshot() = performOnHide {
            FinchCore.implementation.takeScreenshot { }
        }
    }
}
