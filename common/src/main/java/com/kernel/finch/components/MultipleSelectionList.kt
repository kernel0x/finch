package com.kernel.finch.components

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.contracts.component.ValueWrapperComponent
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class MultipleSelectionList<T : FinchListItem>(
    val title: (Set<T>) -> Text,
    val items: List<T>,
    val initiallySelectedItemIds: Set<String>,
    override val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    override val isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
    override val shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
    override val id: String = Component.randomId,
    val onSelectionChanged: (selectedItems: Set<T>) -> Unit = {}
) : ExpandableComponent<MultipleSelectionList<T>>,
    ValueWrapperComponent<Set<String>, MultipleSelectionList<T>> {

    constructor(
        title: CharSequence,
        items: List<T>,
        initiallySelectedItemIds: Set<String>,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId,
        onSelectionChanged: (selectedItems: Set<T>) -> Unit = {}
    ) : this(
        title = { Text.CharSequence(title) },
        items = items,
        initiallySelectedItemIds = initiallySelectedItemIds,
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
        initiallySelectedItemIds: Set<String>,
        isEnabled: Boolean = DEFAULT_IS_ENABLED,
        isValuePersisted: Boolean = DEFAULT_IS_VALUE_PERSISTED,
        shouldRequireConfirmation: Boolean = DEFAULT_SHOULD_REQUIRE_CONFIRMATION,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId,
        onSelectionChanged: (selectedItems: Set<T>) -> Unit = {}
    ) : this(
        title = { Text.ResourceId(title) },
        items = items,
        initiallySelectedItemIds = initiallySelectedItemIds,
        isEnabled = isEnabled,
        isValuePersisted = isValuePersisted,
        shouldRequireConfirmation = shouldRequireConfirmation,
        isExpandedInitially = isExpandedInitially,
        id = id,
        onSelectionChanged = onSelectionChanged
    )

    override val onValueChanged: (newValue: Set<String>) -> Unit =
        { newValue -> onSelectionChanged(newValue.toItems()) }
    override val initialValue: Set<String> = initiallySelectedItemIds
    override val text: (Set<String>) -> Text = { title(it.toItems()) }

    override fun getHeaderTitle(finch: Finch) = title(getCurrentValue(finch).toItems())

    private fun Set<String>?.toItems() =
        orEmpty().mapNotNull { itemId -> items.firstOrNull { it.id == itemId } }.toSet()

    companion object {
        private const val DEFAULT_IS_ENABLED = true
        private const val DEFAULT_IS_VALUE_PERSISTED = false
        private const val DEFAULT_SHOULD_REQUIRE_CONFIRMATION = false
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
    }
}
