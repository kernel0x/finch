package com.kernel.finch.core.list.delegates

import android.os.Build
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.special.ScreenRecording
import com.kernel.finch.core.util.createTextComponentFromType
import com.kernel.finch.core.util.performOnHide

internal class ScreenRecordingDelegate : Component.Delegate<ScreenRecording> {

    override fun createCells(component: ScreenRecording): List<Cell<*>> = listOf(
        createTextComponentFromType(
            type = component.type,
            id = component.id,
            text = component.text,
            isEnabled = component.isEnabled,
            icon = component.icon,
            onItemSelected = {
                component.onButtonPressed()
                hideDebugMenuAndRecordScreen()
            }
        )
    )

    companion object {
        fun hideDebugMenuAndRecordScreen() = performOnHide {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                FinchCore.implementation.recordScreen { }
            }
        }
    }
}
