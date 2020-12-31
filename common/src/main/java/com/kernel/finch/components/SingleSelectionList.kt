package com.kernel.finch.components

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.contracts.component.ValueWrapperComponent
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class SingleSelectionList<T : FinchListItem>(
    val title: (T?) -> Text,
    val items: List<T>,
    val initiallySelectedItemId: String?,
    override val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    override val isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
    override val shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
    override val id: String = Component.randomId,
    val onSelectionChanged: (selectedItem: T?) -> Unit = {}
) : ExpandableComponent<SingleSelectionList<T>>,
    ValueWrapperComponent<String, SingleSelectionList<T>> {

    constructor(
        title: CharSequence,
        items: List<T>,
        initiallySelectedItemId: String?,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId,
        onSelectionChanged: (selectedItem: T?) -> Unit = {}
    ) : this(
        title = { Text.CharSequence(title) },
        items = items,
        initiallySelectedItemId = initiallySelectedItemId,
        isEnabled = isEnabled,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        isExpandedInitially = isExpandedInitially,
        id = id,
        onSelectionChanged = onSelectionChanged
    )

    constructor(
        @StringRes title: Int,
        items: List<T>,
        initiallySelectedItemId: String?,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId,
        onSelectionChanged: (selectedItem: T?) -> Unit = {}
    ) : this(
        title = { Text.ResourceId(title) },
        items = items,
        initiallySelectedItemId = initiallySelectedItemId,
        isEnabled = isEnabled,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        isExpandedInitially = isExpandedInitially,
        id = id,
        onSelectionChanged = onSelectionChanged
    )

    override val initialValue = initiallySelectedItemId.orEmpty()

    override val onValueChanged: (newValue: String) -> Unit =
        { newValue -> onSelectionChanged(newValue.toItem()) }

    override val text: (String) -> Text = { title(it.toItem()) }

    override fun getHeaderTitle(finch: Finch) = title(getCurrentValue(finch).toItem())

    private fun String?.toItem() = items.firstOrNull { it.id == this }

    companion object {
        private const val DEFAULT_IS_ENABLED = true
        private const val DEFAULT_IS_VALUE_PERSISTED = false
        private const val DEFAULT_SHOULD_REQUIRE_CONFIRMATION = false
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
    }
}
