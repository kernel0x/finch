package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.common.models.Text
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.setText
import com.kernel.finch.utils.extensions.tintedDrawable

internal data class ExpandableHeaderCell(
    override val id: String,
    private val text: Text,
    val isExpanded: Boolean,
    val canExpand: Boolean,
    val onItemSelected: () -> Unit
) : Cell<ExpandableHeaderCell> {

    override fun createViewHolderDelegate() = object : ViewHolder.Delegate<ExpandableHeaderCell>() {

        override fun createViewHolder(parent: ViewGroup) = TextViewHolder(parent)
    }

    private class TextViewHolder(parent: ViewGroup) :
        ViewHolder<ExpandableHeaderCell>(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.finch_cell_expandable_header, parent, false)
        ) {

        private val textView = itemView.findViewById<TextView>(R.id.finch_text_view)
        private val drawableExpand by lazy {
            itemView.context.tintedDrawable(
                R.drawable.finch_ic_expand,
                textView.textColors.defaultColor
            )
        }
        private val drawableCollapse by lazy {
            itemView.context.tintedDrawable(
                R.drawable.finch_ic_collapse,
                textView.textColors.defaultColor
            )
        }
        private val drawableEmpty by lazy {
            itemView.context.tintedDrawable(
                R.drawable.finch_ic_empty,
                textView.textColors.defaultColor
            )
        }

        override fun bind(model: ExpandableHeaderCell) {
            textView.run {
                setText(model.text)
                setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    if (model.canExpand) if (model.isExpanded) drawableCollapse else drawableExpand else drawableEmpty,
                    null
                )
                setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        model.onItemSelected()
                    }
                }
                isClickable = model.canExpand
            }
        }
    }
}
