package com.kernel.finch.components.special

import androidx.annotation.DrawableRes
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.Label

data class AppInfo(
    val text: Text = Text.CharSequence(DEFAULT_TEXT),
    val shouldOpenInNewTask: Boolean = DEFAULT_SHOULD_OPEN_IN_NEW_TASK,
    val type: Label.Type = DEFAULT_TYPE,
    @DrawableRes val icon: Int? = DEFAULT_ICON,
    val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    val onButtonPressed: () -> Unit = DEFAULT_ON_BUTTON_PRESSED
) : Component<AppInfo> {

    override val id: String = ID

    companion object {
        const val ID = "appInfo"
        private const val DEFAULT_TEXT = "App info"
        private const val DEFAULT_SHOULD_OPEN_IN_NEW_TASK = false
        private val DEFAULT_TYPE = Label.Type.BUTTON
        private val DEFAULT_ICON: Int? = null
        private const val DEFAULT_IS_ENABLED = true
        private val DEFAULT_ON_BUTTON_PRESSED: () -> Unit = {}
    }
}
