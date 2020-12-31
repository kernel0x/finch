package com.kernel.finch.core.manager.listener

import android.graphics.Canvas
import android.os.Build
import com.kernel.finch.FinchCore
import com.kernel.finch.common.listeners.OverlayListener
import com.kernel.finch.common.models.Inset

internal class OverlayListenerManager : BaseListenerManager<OverlayListener>() {

    fun notifyListeners(canvas: Canvas) {
        var leftInset = 0
        var topInset = 0
        var rightInset = 0
        var bottomInset = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FinchCore.implementation.currentActivity?.window?.decorView?.rootWindowInsets?.let {
                leftInset = it.systemWindowInsetLeft
                topInset = it.systemWindowInsetTop
                rightInset = it.systemWindowInsetRight
                bottomInset = it.systemWindowInsetBottom
            }
        }
        val insets = Inset(leftInset, topInset, rightInset, bottomInset)
        notifyListeners { it.onDrawOver(canvas, insets) }
    }

    override fun addListener(listener: OverlayListener) {
        super.addListener(listener)
        FinchCore.implementation.invalidateOverlay()
    }

    override fun onListenerRemoved() = FinchCore.implementation.invalidateOverlay()
}
