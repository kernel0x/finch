package com.kernel.finch.core.list.cells

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.ViewHolder
import com.kernel.finch.common.models.Text
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.setText
import com.kernel.finch.utils.extensions.dimension
import com.kernel.finch.utils.extensions.tintedDrawable

internal data class ButtonCell(
    override val id: String,
    private val text: Text,
    private val isEnabled: Boolean,
    @DrawableRes private val icon: Int?, //TODO: Not handled.
    private val onButtonPressed: (() -> Unit)?
) : Cell<ButtonCell> {

    override fun createViewHolderDelegate() = object : ViewHolder.Delegate<ButtonCell>() {

        override fun createViewHolder(parent: ViewGroup) = ButtonViewHolder(parent)
    }

    private class ButtonViewHolder(parent: ViewGroup) : ViewHolder<ButtonCell>(
        LayoutInflater.from(parent.context).inflate(R.layout.finch_cell_button, parent, false)
    ) {

        private val button = itemView.findViewById<Button>(R.id.finch_button)
        private val normalHorizontalPadding =
            itemView.context.dimension(R.dimen.finch_item_horizontal_margin)
        private val iconHorizontalPadding =
            itemView.context.dimension(R.dimen.finch_content_padding_small)
        private val drawablePadding =
            itemView.context.dimension(R.dimen.finch_content_padding_large)

        override fun bind(model: ButtonCell) = button.run {
            setText(model.text)
            setCompoundDrawablesWithIntrinsicBounds(model.icon?.let { icon ->
                context.tintedDrawable(
                    icon,
                    textColors.defaultColor
                )
            }, null, null, null)
            setPadding(
                if (model.icon == null) normalHorizontalPadding else iconHorizontalPadding,
                paddingTop,
                normalHorizontalPadding,
                paddingBottom
            )
            compoundDrawablePadding = drawablePadding
            setOnClickListener {
                adapterPosition.let { bindingAdapterPosition ->
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        model.onButtonPressed?.invoke()
                    }
                }
            }
            isClickable = model.onButtonPressed != null
            isEnabled = model.isEnabled
        }
    }
}
