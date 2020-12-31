package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.core.R

internal data class PaddingCell(
    override val id: String
) : Cell<PaddingCell> {

    override fun createViewHolderDelegate() = object : ViewHolder.Delegate<PaddingCell>() {

        override fun createViewHolder(parent: ViewGroup) = PaddingViewHolder(parent)
    }

    private class PaddingViewHolder(parent: ViewGroup) :
        ViewHolder<PaddingCell>(
            LayoutInflater.from(parent.context).inflate(R.layout.finch_cell_padding, parent, false)
        ) {

        override fun bind(model: PaddingCell) = Unit
    }
}
