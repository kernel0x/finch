package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.core.R

internal data class ProgressBarCell(
    override val id: String
) : Cell<ProgressBarCell> {

    override fun createViewHolderDelegate() = object : ViewHolder.Delegate<ProgressBarCell>() {

        override fun createViewHolder(parent: ViewGroup) = LoadingIndicatorViewHolder(parent)
    }

    private class LoadingIndicatorViewHolder(parent: ViewGroup) : ViewHolder<ProgressBarCell>(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.finch_cell_progress_bar, parent, false)
    ) {

        override fun bind(model: ProgressBarCell) = Unit
    }
}
