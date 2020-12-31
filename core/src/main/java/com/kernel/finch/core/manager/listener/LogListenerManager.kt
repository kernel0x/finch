package com.kernel.finch.core.manager.listener

import com.kernel.finch.common.listeners.LogListener

internal class LogListenerManager : BaseListenerManager<LogListener>() {

    fun notifyListeners(tag: String?, message: CharSequence, payload: CharSequence?) =
        notifyListeners { it.onAdded(tag, message, payload) }
}
