package com.kernel.finch.common.listeners

import android.graphics.Canvas
import com.kernel.finch.common.models.Inset

interface OverlayListener {
    fun onDrawOver(canvas: Canvas, insets: Inset)
}
