package com.kernel.finch.components.special

import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text

data class DeviceInfo(
    val title: Text = Text.CharSequence(DEFAULT_TITLE),
    val shouldShowManufacturer: Boolean = DEFAULT_SHOULD_SHOW_MANUFACTURER,
    val shouldShowModel: Boolean = DEFAULT_SHOULD_SHOW_MODEL,
    val shouldShowResolutionsPx: Boolean = DEFAULT_SHOULD_SHOW_RESOLUTION_PX,
    val shouldShowResolutionsDp: Boolean = DEFAULT_SHOULD_SHOW_RESOLUTION_DP,
    val shouldShowDensity: Boolean = DEFAULT_SHOULD_SHOW_DENSITY,
    val shouldShowAndroidVersion: Boolean = DEFAULT_SHOULD_SHOW_ANDROID_VERSION,
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY
) : ExpandableComponent<DeviceInfo> {

    override val id: String = ID

    override fun getHeaderTitle(finch: Finch) = title

    companion object {
        const val ID = "deviceInfo"
        private const val DEFAULT_TITLE = "Device info"
        private const val DEFAULT_SHOULD_SHOW_MANUFACTURER = true
        private const val DEFAULT_SHOULD_SHOW_MODEL = true
        private const val DEFAULT_SHOULD_SHOW_RESOLUTION_PX = true
        private const val DEFAULT_SHOULD_SHOW_RESOLUTION_DP = true
        private const val DEFAULT_SHOULD_SHOW_DENSITY = true
        private const val DEFAULT_SHOULD_SHOW_ANDROID_VERSION = true
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
    }
}
