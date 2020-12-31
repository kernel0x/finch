package com.kernel.finch.core.util

import androidx.annotation.DrawableRes
import com.kernel.finch.FinchCore
import com.kernel.finch.common.listeners.VisibilityListener
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.Label
import com.kernel.finch.core.list.cells.ButtonCell
import com.kernel.finch.core.list.cells.TextCell
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal fun performOnHide(action: () -> Unit) {
    val listener = object : VisibilityListener {
        override fun onHidden() {
            val reference = this
            action()
            GlobalScope.launch {
                delay(100)
                FinchCore.implementation.removeVisibilityListener(reference)
            }
        }
    }
    if (FinchCore.implementation.hide()) {
        FinchCore.implementation.addInternalVisibilityListener(listener)
    } else {
        listener.onHidden()
    }
}

internal fun createTextComponentFromType(
    type: Label.Type,
    id: String,
    text: Text,
    isEnabled: Boolean,
    @DrawableRes icon: Int?,
    onItemSelected: (() -> Unit)?
) = when (type) {
    Label.Type.NORMAL,
    Label.Type.HEADER -> TextCell(
        id = id,
        text = text,
        isEnabled = isEnabled,
        isSectionHeader = type == Label.Type.HEADER,
        icon = icon,
        onItemSelected = onItemSelected
    )
    Label.Type.BUTTON -> ButtonCell(
        id = id,
        text = text,
        isEnabled = isEnabled,
        icon = icon,
        onButtonPressed = onItemSelected
    )
}
