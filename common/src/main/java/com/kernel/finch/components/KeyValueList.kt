package com.kernel.finch.components

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class KeyValueList(
    val title: Text,
    val pairs: List<Pair<Text, Text>>,
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
    override val id: String = Component.randomId
) : ExpandableComponent<KeyValueList> {

    constructor(
        title: CharSequence,
        pairs: List<Pair<CharSequence, CharSequence>>,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId
    ) : this(
        title = Text.CharSequence(title),
        pairs = pairs.map { Text.CharSequence(it.first) to Text.CharSequence(it.second) },
        isExpandedInitially = isExpandedInitially,
        id = id
    )

    constructor(
        @StringRes title: Int,
        pairs: List<Pair<CharSequence, CharSequence>>,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId
    ) : this(
        title = Text.ResourceId(title),
        pairs = pairs.map { Text.CharSequence(it.first) to Text.CharSequence(it.second) },
        isExpandedInitially = isExpandedInitially,
        id = id
    )

    override fun getHeaderTitle(finch: Finch) = title

    companion object {
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
    }
}
