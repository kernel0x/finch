package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.common.models.Text
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.setText

internal data class ExpandedItemRadioButtonCell(
    override val id: String,
    private val text: Text,
    private val isChecked: Boolean,
    private val isEnabled: Boolean,
    private val onValueChanged: (Boolean) -> Unit
) : Cell<ExpandedItemRadioButtonCell> {

    override fun createViewHolderDelegate() =
        object : ViewHolder.Delegate<ExpandedItemRadioButtonCell>() {

            override fun createViewHolder(parent: ViewGroup) = SwitchViewHolder(parent)
        }

    private class SwitchViewHolder(parent: ViewGroup) : ViewHolder<ExpandedItemRadioButtonCell>(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.finch_cell_expanded_item_radio_button, parent, false)
    ) {

        private val radioButton = itemView.findViewById<RadioButton>(R.id.finch_radio_button)

        override fun bind(model: ExpandedItemRadioButtonCell) = radioButton.run {
            setText(model.text)
            setOnCheckedChangeListener(null)
            isChecked = model.isChecked
            isEnabled = model.isEnabled
            setOnCheckedChangeListener { _, isChecked -> model.onValueChanged(isChecked) }
        }
    }
}
