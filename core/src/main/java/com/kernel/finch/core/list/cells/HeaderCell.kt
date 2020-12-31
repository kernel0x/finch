package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.common.models.Text
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.setText
import com.kernel.finch.core.util.extension.visible

internal data class HeaderCell(
    override val id: String,
    private val title: Text,
    private val subtitle: Text?,
    private val text: Text?
) : Cell<HeaderCell> {

    override fun createViewHolderDelegate() = object : ViewHolder.Delegate<HeaderCell>() {

        override fun createViewHolder(parent: ViewGroup) = HeaderViewHolder(parent)
    }

    private class HeaderViewHolder(parent: ViewGroup) : ViewHolder<HeaderCell>(
        LayoutInflater.from(parent.context).inflate(R.layout.finch_cell_header, parent, false)
    ) {

        private val titleTextView = itemView.findViewById<TextView>(R.id.finch_title)
        private val subtitleTextView = itemView.findViewById<TextView>(R.id.finch_subtitle)
        private val textTextView = itemView.findViewById<TextView>(R.id.finch_text)

        override fun bind(model: HeaderCell) {
            titleTextView.setText(model.title)
            subtitleTextView.visible = model.subtitle != null
            subtitleTextView.setText(model.subtitle)
            textTextView.visible = model.text != null
            textTextView.setText(model.text)
        }
    }
}
