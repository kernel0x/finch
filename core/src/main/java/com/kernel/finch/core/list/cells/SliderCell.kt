package com.kernel.finch.core.list.cells

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.common.models.Text
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.setText

internal data class SliderCell(
    override val id: String,
    private val text: Text,
    private val value: Int,
    private val minimumValue: Int,
    private val maximumValue: Int,
    private val isEnabled: Boolean,
    private val onValueChanged: (Int) -> Unit
) : Cell<SliderCell> {

    override fun createViewHolderDelegate() = object : ViewHolder.Delegate<SliderCell>() {

        override fun createViewHolder(parent: ViewGroup) = SliderViewHolder(parent)
    }

    @SuppressLint("ClickableViewAccessibility")
    private class SliderViewHolder(parent: ViewGroup) : ViewHolder<SliderCell>(
        LayoutInflater.from(parent.context).inflate(R.layout.finch_cell_slider, parent, false)
    ) {

        private val textView = itemView.findViewById<TextView>(R.id.finch_text_view)
        private val seekBar = itemView.findViewById<SeekBar>(R.id.finch_seek_bar)

        init {
            seekBar.setOnTouchListener { _, _ ->
                (itemView.parent?.parent as? ViewGroup?)?.requestDisallowInterceptTouchEvent(true)
                false
            }
        }

        override fun bind(model: SliderCell) {
            textView.setText(model.text)
            seekBar.run {
                setOnSeekBarChangeListener(null)
                max = model.maximumValue - model.minimumValue
                progress = model.value - model.minimumValue
                isEnabled = model.isEnabled
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser && progress != model.value + model.minimumValue) {
                            model.onValueChanged(progress + model.minimumValue)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

                    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
                })
            }
        }
    }
}
