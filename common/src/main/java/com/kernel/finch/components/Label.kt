package com.kernel.finch.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class Label(
    val text: Text,
    val type: Type = DEFAULT_TYPE,
    @DrawableRes val icon: Int? = DEFAULT_ICON,
    val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    override val id: String = Component.randomId,
    val onItemSelected: (() -> Unit)? = DEFAULT_ON_ITEM_SELECTED
) : Component<Label> {

    constructor(
        text: CharSequence,
        type: Type = DEFAULT_TYPE,
        @DrawableRes icon: Int? = DEFAULT_ICON,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        id: String = Component.randomId,
        onItemSelected: (() -> Unit)? = DEFAULT_ON_ITEM_SELECTED
    ) : this(
        text = Text.CharSequence(text),
        type = type,
        icon = icon,
        isEnabled = isEnabled,
        id = id,
        onItemSelected = onItemSelected
    )

    constructor(
        @StringRes text: Int,
        type: Type = DEFAULT_TYPE,
        @DrawableRes icon: Int? = DEFAULT_ICON,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        id: String = Component.randomId,
        onItemSelected: (() -> Unit)? = DEFAULT_ON_ITEM_SELECTED
    ) : this(
        text = Text.ResourceId(text),
        type = type,
        icon = icon,
        isEnabled = isEnabled,
        id = id,
        onItemSelected = onItemSelected
    )

    enum class Type {
        NORMAL,
        HEADER,
        BUTTON
    }

    companion object {
        private val DEFAULT_ON_ITEM_SELECTED: (() -> Unit)? = null
        private val DEFAULT_TYPE = Type.NORMAL
        private val DEFAULT_ICON: Int? = null
        private const val DEFAULT_IS_ENABLED = true
    }
}
