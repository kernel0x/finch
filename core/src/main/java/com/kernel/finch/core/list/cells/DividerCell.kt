package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.core.R
import com.kernel.finch.utils.extensions.colorResource

internal data class DividerCell(
    override val id: String
) : Cell<DividerCell> {

    override fun createViewHolderDelegate() = object : ViewHolder.Delegate<DividerCell>() {

        override fun createViewHolder(parent: ViewGroup) = DividerViewHolder(parent)
    }

    private class DividerViewHolder(parent: ViewGroup) : ViewHolder<DividerCell>(
        LayoutInflater.from(parent.context).inflate(R.layout.finch_cell_divider, parent, false)
    ) {

        override fun bind(model: DividerCell) =
            itemView.setBackgroundColor(itemView.context.colorResource(android.R.attr.textColorPrimary))
    }
}
