package com.kernel.finch.components.special

import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.kernel.finch.common.contracts.component.ValueWrapperComponent
import com.kernel.finch.common.models.Inset
import com.kernel.finch.common.models.Text

data class DesignOverlay(
    override val text: (Boolean) -> Text = { Text.CharSequence(DEFAULT_TEXT) },
    @Dimension val grid: Int? = DEFAULT_GRID,
    @Dimension val overlayPrimary: Int? = DEFAULT_OVERLAY_PRIMARY,
    @Dimension val overlaySecondary: Int? = DEFAULT_OVERLAY_SECONDARY,
    @ColorInt val color: Int? = DEFAULT_COLOR,
    override val initialValue: Boolean = DEFAULT_INITIAL_VALUE,
    override val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    override val isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
    val applyInsets: ((windowInsets: Inset) -> Inset)? = DEFAULT_APPLY_INSETS,
    override val onValueChanged: (Boolean) -> Unit = DEFAULT_ON_VALUE_CHANGED
) : ValueWrapperComponent<Boolean, DesignOverlay> {

    override val id: String = ID
    override val shouldRequireConfirmation = false

    companion object {
        const val ID = "designOverlay"
        private const val DEFAULT_TEXT = "Design overlay"
        private val DEFAULT_GRID: Int? = null
        private val DEFAULT_OVERLAY_PRIMARY: Int? = null
        private val DEFAULT_OVERLAY_SECONDARY: Int? = null
        private val DEFAULT_COLOR: Int? = null
        private const val DEFAULT_INITIAL_VALUE = false
        private const val DEFAULT_IS_ENABLED = true
        private const val DEFAULT_IS_VALUE_PERSISTED = false
        private val DEFAULT_APPLY_INSETS: ((windowInsets: Inset) -> Inset)? = null
        private val DEFAULT_ON_VALUE_CHANGED: (Boolean) -> Unit = {}
    }
}
