package com.kernel.finch.core.list.delegates

import android.content.Intent
import android.provider.Settings
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.special.DeveloperOptions
import com.kernel.finch.core.util.createTextComponentFromType

internal class DeveloperOptionsDelegate : Component.Delegate<DeveloperOptions> {

    override fun createCells(component: DeveloperOptions): List<Cell<*>> = listOf(
        createTextComponentFromType(
            type = component.type,
            id = component.id,
            text = component.text,
            isEnabled = component.isEnabled,
            icon = component.icon,
            onItemSelected = {
                FinchCore.implementation.currentActivity?.run {
                    startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                        if (component.shouldOpenInNewTask) {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    })
                }
                component.onButtonPressed()
            }
        )
    )
}
