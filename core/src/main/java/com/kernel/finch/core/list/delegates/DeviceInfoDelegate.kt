package com.kernel.finch.core.list.delegates

import android.os.Build
import android.util.DisplayMetrics
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.special.DeviceInfo
import com.kernel.finch.core.list.cells.ExpandedItemKeyValueCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate
import kotlin.math.roundToInt

internal class DeviceInfoDelegate : ExpandableComponentDelegate<DeviceInfo> {

    override fun canExpand(component: DeviceInfo) = component.shouldShowManufacturer
        || component.shouldShowModel
        || component.shouldShowResolutionsPx
        || component.shouldShowResolutionsDp
        || component.shouldShowDensity
        || component.shouldShowAndroidVersion

    override fun MutableList<Cell<*>>.addItems(component: DeviceInfo) {
        if (component.shouldShowManufacturer) {
            add(
                ExpandedItemKeyValueCell(
                    id = "${component.id}_manufacturer",
                    key = "Manufacturer",
                    value = Build.MANUFACTURER
                )
            )
        }
        if (component.shouldShowModel) {
            add(
                ExpandedItemKeyValueCell(
                    id = "${component.id}_model",
                    key = "Model",
                    value = Build.MODEL
                )
            )
        }
        val dm = DisplayMetrics()
        FinchCore.implementation.currentActivity?.windowManager?.defaultDisplay?.getMetrics(dm)
            ?.let {
                if (component.shouldShowResolutionsPx) {
                    add(
                        ExpandedItemKeyValueCell(
                            id = "${component.id}_resolution_px",
                            key = "Resolution (px)",
                            value = "${dm.widthPixels} * ${dm.heightPixels}"
                        )
                    )
                    if (component.shouldShowResolutionsDp) {
                        add(
                            ExpandedItemKeyValueCell(
                                id = "${component.id}_resolution_dp",
                                key = "Resolution (dp)",
                                value = "${(dm.widthPixels / dm.density).roundToInt()} * ${(dm.heightPixels / dm.density).roundToInt()}"
                            )
                        )
                    }
                    if (component.shouldShowDensity) {
                        add(
                            ExpandedItemKeyValueCell(
                                id = "${component.id}_density",
                                key = "Density (dpi)",
                                value = "${dm.densityDpi}"
                            )
                        )
                    }
                }
            }
        if (component.shouldShowAndroidVersion) {
            add(
                ExpandedItemKeyValueCell(
                    id = "${component.id}_sdkVersion",
                    key = "Android SDK version",
                    value = Build.VERSION.SDK_INT.toString()
                )
            )
        }
    }
}
