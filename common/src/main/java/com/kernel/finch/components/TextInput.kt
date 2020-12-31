package com.kernel.finch.components

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ValueWrapperComponent
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class TextInput(
    override val text: (String) -> Text,
    override val initialValue: String = DEFAULT_INITIAL_VALUE,
    override val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    val areRealTimeUpdatesEnabled: Boolean = DEFAULT_ARE_REAL_TIME_UPDATES_ENABLED,
    val doneText: Text = Text.CharSequence(DEFAULT_DONE_TEXT),
    val cancelText: Text = Text.CharSequence(DEFAULT_CANCEL_TEXT),
    override val isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
    override val shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
    override val id: String = Component.randomId,
    val validator: (String) -> Boolean = DEFAULT_VALIDATOR,
    override val onValueChanged: (String) -> Unit = DEFAULT_ON_VALUE_CHANGED
) : ValueWrapperComponent<String, TextInput> {

    constructor(
        text: CharSequence,
        initialValue: String = DEFAULT_INITIAL_VALUE,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        areRealTimeUpdatesEnabled: Boolean = DEFAULT_ARE_REAL_TIME_UPDATES_ENABLED,
        doneText: Text = Text.CharSequence(DEFAULT_DONE_TEXT),
        cancelText: Text = Text.CharSequence(DEFAULT_CANCEL_TEXT),
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        id: String = Component.randomId,
        validator: (String) -> Boolean = DEFAULT_VALIDATOR,
        onValueChanged: (String) -> Unit = DEFAULT_ON_VALUE_CHANGED
    ) : this(
        text = { Text.CharSequence(text) },
        initialValue = initialValue,
        isEnabled = isEnabled,
        areRealTimeUpdatesEnabled = areRealTimeUpdatesEnabled,
        doneText = doneText,
        cancelText = cancelText,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        id = id,
        validator = validator,
        onValueChanged = onValueChanged
    )

    constructor(
        @StringRes text: Int,
        initialValue: String = DEFAULT_INITIAL_VALUE,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        areRealTimeUpdatesEnabled: Boolean = DEFAULT_ARE_REAL_TIME_UPDATES_ENABLED,
        doneText: Text = Text.CharSequence(DEFAULT_DONE_TEXT),
        cancelText: Text = Text.CharSequence(DEFAULT_CANCEL_TEXT),
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        id: String = Component.randomId,
        validator: (String) -> Boolean = DEFAULT_VALIDATOR,
        onValueChanged: (String) -> Unit = DEFAULT_ON_VALUE_CHANGED
    ) : this(
        text = { Text.ResourceId(text) },
        initialValue = initialValue,
        isEnabled = isEnabled,
        areRealTimeUpdatesEnabled = areRealTimeUpdatesEnabled,
        doneText = doneText,
        cancelText = cancelText,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        id = id,
        validator = validator,
        onValueChanged = onValueChanged
    )

    companion object {
        private const val DEFAULT_INITIAL_VALUE = ""
        private const val DEFAULT_IS_ENABLED = true
        private const val DEFAULT_ARE_REAL_TIME_UPDATES_ENABLED = true
        private const val DEFAULT_DONE_TEXT = "Done"
        private const val DEFAULT_CANCEL_TEXT = "Cancel"
        private const val DEFAULT_IS_VALUE_PERSISTED = false
        private const val DEFAULT_SHOULD_REQUIRE_CONFIRMATION = false
        private val DEFAULT_VALIDATOR: (String) -> Boolean = { true }
        private val DEFAULT_ON_VALUE_CHANGED: (String) -> Unit = {}
    }
}
