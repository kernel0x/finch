package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.common.models.Text
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.setText

internal data class ExpandedItemCheckBoxCell(
    override val id: String,
    private val text: Text,
    private val isChecked: Boolean,
    private val isEnabled: Boolean,
    private val onValueChanged: (Boolean) -> Unit
) : Cell<ExpandedItemCheckBoxCell> {

    override fun createViewHolderDelegate() =
        object : ViewHolder.Delegate<ExpandedItemCheckBoxCell>() {

            override fun createViewHolder(parent: ViewGroup) = CheckBoxViewHolder(parent)
        }

    private class CheckBoxViewHolder(parent: ViewGroup) :
        ViewHolder<ExpandedItemCheckBoxCell>(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.finch_cell_expanded_item_check_box, parent, false)
        ) {

        private val checkBox = itemView.findViewById<CheckBox>(R.id.finch_check_box)

        override fun bind(model: ExpandedItemCheckBoxCell) {
            checkBox.run {
                setText(model.text)
                setOnCheckedChangeListener(null)
                isChecked = model.isChecked
                isEnabled = model.isEnabled
                setOnCheckedChangeListener { _, isChecked -> model.onValueChanged(isChecked) }
            }
        }
    }
}
