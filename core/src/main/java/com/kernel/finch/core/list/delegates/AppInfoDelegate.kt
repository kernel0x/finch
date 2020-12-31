package com.kernel.finch.core.list.delegates

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.special.AppInfo
import com.kernel.finch.core.util.createTextComponentFromType

internal class AppInfoDelegate : Component.Delegate<AppInfo> {

    override fun createCells(component: AppInfo): List<Cell<*>> = listOf(
        createTextComponentFromType(
            type = component.type,
            id = component.id,
            text = component.text,
            isEnabled = component.isEnabled,
            icon = component.icon,
            onItemSelected = {
                FinchCore.implementation.currentActivity?.run {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", packageName, null)
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
