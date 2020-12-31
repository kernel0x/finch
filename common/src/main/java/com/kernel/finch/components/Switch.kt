package com.kernel.finch.components

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ValueWrapperComponent
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class Switch(
    override val text: (Boolean) -> Text,
    override val initialValue: Boolean = DEFAULT_INITIAL_VALUE,
    override val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    override val isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
    override val shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
    override val id: String = Component.randomId,
    override val onValueChanged: (Boolean) -> Unit = DEFAULT_ON_VALUE_CHANGED
) : ValueWrapperComponent<Boolean, Switch> {

    constructor(
        text: CharSequence,
        initialValue: Boolean = DEFAULT_INITIAL_VALUE,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        id: String = Component.randomId,
        onValueChanged: (Boolean) -> Unit = DEFAULT_ON_VALUE_CHANGED
    ) : this(
        text = { Text.CharSequence(text) },
        initialValue = initialValue,
        isEnabled = isEnabled,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        id = id,
        onValueChanged = onValueChanged
    )

    constructor(
        @StringRes text: Int,
        initialValue: Boolean = DEFAULT_INITIAL_VALUE,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        id: String = Component.randomId,
        onValueChanged: (Boolean) -> Unit = DEFAULT_ON_VALUE_CHANGED
    ) : this(
        text = { Text.ResourceId(text) },
        initialValue = initialValue,
        isEnabled = isEnabled,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        id = id,
        onValueChanged = onValueChanged
    )

    companion object {
        private const val DEFAULT_INITIAL_VALUE = false
        private const val DEFAULT_IS_ENABLED = true
        private const val DEFAULT_IS_VALUE_PERSISTED = false
        private const val DEFAULT_SHOULD_REQUIRE_CONFIRMATION = false
        private val DEFAULT_ON_VALUE_CHANGED: (Boolean) -> Unit = {}
    }
}
