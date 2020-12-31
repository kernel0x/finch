package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.common.models.Text
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.setText

internal data class SwitchCell(
    override val id: String,
    private val text: Text,
    private val isChecked: Boolean,
    private val isEnabled: Boolean,
    private val onValueChanged: (Boolean) -> Unit
) : Cell<SwitchCell> {

    override fun createViewHolderDelegate() = object : ViewHolder.Delegate<SwitchCell>() {

        override fun createViewHolder(parent: ViewGroup) = SwitchViewHolder(parent)
    }

    private class SwitchViewHolder(parent: ViewGroup) : ViewHolder<SwitchCell>(
        LayoutInflater.from(parent.context).inflate(R.layout.finch_cell_switch, parent, false)
    ) {

        private val switch = itemView.findViewById<SwitchCompat>(R.id.finch_switch)

        override fun bind(model: SwitchCell) = switch.run {
            setText(model.text)
            setOnCheckedChangeListener(null)
            isChecked = model.isChecked
            isEnabled = model.isEnabled
            setOnCheckedChangeListener { _, isChecked -> model.onValueChanged(isChecked) }
        }
    }
}
