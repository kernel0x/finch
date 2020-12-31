package com.kernel.finch.components

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class LongText(
    val title: Text,
    val text: Text,
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
    override val id: String = Component.randomId
) : ExpandableComponent<LongText> {

    constructor(
        title: CharSequence,
        text: CharSequence,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId
    ) : this(
        title = Text.CharSequence(title),
        text = Text.CharSequence(text),
        isExpandedInitially = isExpandedInitially,
        id = id
    )

    constructor(
        @StringRes title: Int,
        @StringRes text: Int,
        isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY,
        id: String = Component.randomId
    ) : this(
        title = Text.ResourceId(title),
        text = Text.ResourceId(text),
        isExpandedInitially = isExpandedInitially,
        id = id
    )

    override fun getHeaderTitle(finch: Finch) = title

    companion object {
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
    }
}
