package com.kernel.finch.core.list.delegates

import android.os.Build
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.special.ScreenCaptureToolbox
import com.kernel.finch.core.list.cells.ExpandedItemTextCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate

internal class ScreenCaptureToolboxDelegate : ExpandableComponentDelegate<ScreenCaptureToolbox> {

    override fun canExpand(component: ScreenCaptureToolbox) = true

    override fun MutableList<Cell<*>>.addItems(component: ScreenCaptureToolbox) {
        component.imageText?.let { imageText ->
            add(
                ExpandedItemTextCell(
                    id = "${component.id}_image",
                    text = imageText,
                    isEnabled = true,
                    onItemSelected = ScreenshotDelegate.Companion::hideDebugMenuAndTakeScreenshot
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            component.videoText?.let { videoText ->
                add(
                    ExpandedItemTextCell(
                        id = "${component.id}_video",
                        text = videoText,
                        isEnabled = true,
                        onItemSelected = ScreenRecordingDelegate.Companion::hideDebugMenuAndRecordScreen
                    )
                )
            }
        }
        add(
            ExpandedItemTextCell(
                id = "${component.id}_gallery",
                text = component.galleryText,
                isEnabled = true,
                onItemSelected = FinchCore.implementation::openGallery
            )
        )
    }
}
