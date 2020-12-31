package com.kernel.finch.core.list.delegates

import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.LongText
import com.kernel.finch.core.list.cells.ExpandedItemTextCell
import com.kernel.finch.core.list.delegates.shared.ExpandableComponentDelegate

internal class LongTextDelegate : ExpandableComponentDelegate<LongText> {

    override fun canExpand(component: LongText) = when (val text = component.text) {
        is Text.CharSequence -> text.charSequence.isNotBlank()
        is Text.ResourceId -> true
    }

    override fun MutableList<Cell<*>>.addItems(component: LongText) {
        add(
            ExpandedItemTextCell(
                id = "text_${component.id}",
                text = component.text,
                isEnabled = true,
                onItemSelected = null
            )
        )
    }
}
