package com.kernel.finch.core.list.delegates

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.listeners.OverlayListener
import com.kernel.finch.common.models.Inset
import com.kernel.finch.components.special.DesignOverlay
import com.kernel.finch.core.R
import com.kernel.finch.core.list.cells.SwitchCell
import com.kernel.finch.core.list.delegates.shared.ValueWrapperComponentDelegate
import com.kernel.finch.utils.extensions.colorResource
import com.kernel.finch.utils.extensions.dimension

internal class DesignOverlayDelegate :
    ValueWrapperComponentDelegate.Boolean<DesignOverlay>() {

    private var component: DesignOverlay? = null
    private val gridPaint = Paint()
    private val overlayPaint = Paint()
    private var overlayGrid = 0
    private var overlayPrimary = 0f
    private var overlaySecondary = 0f

    init {
        FinchCore.implementation.addInternalOverlayListener(object : OverlayListener {
            override fun onDrawOver(canvas: Canvas, insets: Inset) {
                if (component == null) {
                    FinchCore.implementation.currentActivity?.let { tryToInitialize(it) }
                }
                component?.let {
                    val processedInsets = it.applyInsets?.invoke(insets) ?: insets
                    canvas.drawGridIfNeeded(
                        it,
                        processedInsets.left,
                        processedInsets.top,
                        processedInsets.right,
                        processedInsets.bottom
                    )
                }
            }
        })
    }

    override fun createCells(component: DesignOverlay): List<Cell<*>> =
        getCurrentValue(component).let { currentValue ->
            listOf(
                SwitchCell(
                    id = component.id,
                    text = component.text(currentValue),
                    isChecked = currentValue,
                    isEnabled = component.isEnabled,
                    onValueChanged = { newValue -> setCurrentValue(component, newValue) }
                )
            )
        }

    override fun callOnValueChanged(component: DesignOverlay, newValue: kotlin.Boolean) {
        FinchCore.implementation.invalidateOverlay()
        super.callOnValueChanged(component, newValue)
    }

    private fun tryToInitialize(context: Context) {
        FinchCore.implementation.find<DesignOverlay>(DesignOverlay.ID)
            ?.let { component ->
                this.component = component
                gridPaint.strokeWidth = context.dimension(R.dimen.finch_divider_size).toFloat()
                overlayPaint.strokeWidth = context.dimension(R.dimen.finch_divider_size).toFloat()
                overlayGrid =
                    component.grid ?: context.dimension(R.dimen.finch_default_design_overlay_grid)
                overlayPrimary = (component.overlayPrimary
                    ?: context.dimension(R.dimen.finch_default_design_overlay_primary)).toFloat()
                overlaySecondary = (component.overlaySecondary
                    ?: context.dimension(R.dimen.finch_default_design_overlay_secondary)).toFloat()
            }
    }

    //TODO: Does not work well for RTL layouts.
    private fun Canvas.drawGridIfNeeded(
        component: DesignOverlay,
        leftInset: Int,
        topInset: Int,
        rightInset: Int,
        bottomInset: Int
    ) {
        if (component.getCurrentValue(FinchCore.implementation) == true) {
            gridPaint.color = component.color
                ?: FinchCore.implementation.currentActivity?.colorResource(android.R.attr.textColorPrimary)
                    ?: gridPaint.color
            overlayPaint.color = gridPaint.color
            gridPaint.alpha = GRID_ALPHA
            overlayPaint.alpha = OVERLAY_ALPHA
            for (x in 0..width step overlayGrid) {
                drawLine(
                    leftInset + x.toFloat(),
                    topInset.toFloat(),
                    leftInset + x.toFloat(),
                    height.toFloat() - bottomInset,
                    gridPaint
                )
            }
            for (y in 0..height step overlayGrid) {
                drawLine(
                    leftInset.toFloat(),
                    topInset + y.toFloat(),
                    width.toFloat() - rightInset,
                    topInset + y.toFloat(),
                    gridPaint
                )
            }
            drawLine(
                leftInset + overlayPrimary,
                0f,
                leftInset + overlayPrimary,
                height.toFloat(),
                overlayPaint
            )
            drawLine(
                leftInset + overlaySecondary,
                0f,
                leftInset + overlaySecondary,
                height.toFloat(),
                overlayPaint
            )
            drawLine(
                width - overlayPrimary - rightInset,
                0f,
                width - overlayPrimary - rightInset,
                height.toFloat(),
                overlayPaint
            )
        }
    }

    companion object {
        private const val GRID_ALPHA = 32
        private const val OVERLAY_ALPHA = 64
    }
}
