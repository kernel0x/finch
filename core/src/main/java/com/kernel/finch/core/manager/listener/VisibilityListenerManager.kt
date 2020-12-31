package com.kernel.finch.core.manager.listener

import com.kernel.finch.common.listeners.VisibilityListener

internal class VisibilityListenerManager : BaseListenerManager<VisibilityListener>() {

    //TODO: The way visibility listeners are notified is buggy during configuration changes
    private var isDebugMenuVisible = false

    fun notifyListenersOnShow() {
        if (!isDebugMenuVisible) {
            notifyListeners { it.onShown() }
            isDebugMenuVisible = true
        }
    }

    fun notifyListenersOnHide() {
        if (isDebugMenuVisible) {
            notifyListeners { it.onHidden() }
            isDebugMenuVisible = false
        }
    }
}