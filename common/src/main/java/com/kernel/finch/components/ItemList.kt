package com.kernel.finch.components

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class ItemList<T : FinchListItem>(
    val title: Text,
    val items: List<T>,
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
    override val id: String = Component.randomId,
    val onItemSelected: ((item: T) -> Unit)? = DEFAULT_IN_ITEM_SELECTED
) : ExpandableComponent<ItemList<T>> {

    constructor(
        title: CharSequence,
        items: List<T>,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId,
        onItemSelected: ((item: T) -> Unit)? = DEFAULT_IN_ITEM_SELECTED
    ) : this(
        title = Text.CharSequence(title),
        items = items,
        isExpandedInitially = isExpandedInitially,
        id = id,
        onItemSelected = onItemSelected
    )

    constructor(
        @StringRes title: Int,
        items: List<T>,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId,
        onItemSelected: ((item: T) -> Unit)? = DEFAULT_IN_ITEM_SELECTED
    ) : this(
        title = Text.ResourceId(title),
        items = items,
        isExpandedInitially = isExpandedInitially,
        id = id,
        onItemSelected = onItemSelected
    )

    override fun getHeaderTitle(finch: Finch) = title

    companion object {
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
        private val DEFAULT_IN_ITEM_SELECTED = null
    }
}
