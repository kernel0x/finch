package com.kernel.finch.components.special

import com.kernel.finch.common.contracts.component.ValueWrapperComponent
import com.kernel.finch.common.models.Text

data class AnimationSpeed(
    override val text: (Boolean) -> Text = { Text.CharSequence(DEFAULT_TEXT) },
    val multiplier: Float = DEFAULT_MULTIPLIER,
    override val initialValue: Boolean = DEFAULT_INITIAL_VALUE,
    override val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    override val isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
    override val onValueChanged: (Boolean) -> Unit = DEFAULT_ON_VALUE_CHANGED
) : ValueWrapperComponent<Boolean, AnimationSpeed> {

    override val id: String = ID
    override val shouldRequireConfirmation = false

    companion object {
        const val ID = "animationSpeed"
        private const val DEFAULT_TEXT = "Slow animations"
        private const val DEFAULT_MULTIPLIER = 4f
        private const val DEFAULT_INITIAL_VALUE = false
        private const val DEFAULT_IS_ENABLED = true
        private const val DEFAULT_IS_VALUE_PERSISTED = false
        private val DEFAULT_ON_VALUE_CHANGED: (Boolean) -> Unit = {}
    }
}
