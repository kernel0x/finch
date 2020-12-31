package com.kernel.finch.components

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ValueWrapperComponent
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class Slider(
    override val text: (Int) -> Text,
    val minimumValue: Int = DEFAULT_MINIMUM_VALUE,
    val maximumValue: Int = DEFAULT_MAXIMUM_VALUE,
    override val initialValue: Int = minimumValue,
    override val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    override val isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
    override val shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
    override val id: String = Component.randomId,
    override val onValueChanged: (Int) -> Unit = DEFAULT_ON_VALUE_CHANGED
) : ValueWrapperComponent<Int, Slider> {

    constructor(
        text: CharSequence,
        minimumValue: Int = DEFAULT_MINIMUM_VALUE,
        maximumValue: Int = DEFAULT_MAXIMUM_VALUE,
        initialValue: Int = minimumValue,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        id: String = Component.randomId,
        onValueChanged: (Int) -> Unit = DEFAULT_ON_VALUE_CHANGED
    ) : this(
        text = { Text.CharSequence(text) },
        minimumValue = minimumValue,
        maximumValue = maximumValue,
        initialValue = initialValue,
        isEnabled = isEnabled,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        id = id,
        onValueChanged = onValueChanged
    )

    constructor(
        @StringRes text: Int,
        minimumValue: Int = DEFAULT_MINIMUM_VALUE,
        maximumValue: Int = DEFAULT_MAXIMUM_VALUE,
        initialValue: Int = minimumValue,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        id: String = Component.randomId,
        onValueChanged: (Int) -> Unit = DEFAULT_ON_VALUE_CHANGED
    ) : this(
        text = { Text.ResourceId(text) },
        minimumValue = minimumValue,
        maximumValue = maximumValue,
        initialValue = initialValue,
        isEnabled = isEnabled,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        id = id,
        onValueChanged = onValueChanged
    )

    companion object {
        private const val DEFAULT_MINIMUM_VALUE = 0
        private const val DEFAULT_MAXIMUM_VALUE = 10
        private const val DEFAULT_IS_ENABLED = true
        private const val DEFAULT_IS_VALUE_PERSISTED = false
        private const val DEFAULT_SHOULD_REQUIRE_CONFIRMATION = false
        private val DEFAULT_ON_VALUE_CHANGED: (Int) -> Unit = {}
    }
}
