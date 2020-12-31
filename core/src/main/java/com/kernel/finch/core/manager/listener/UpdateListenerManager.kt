package com.kernel.finch.core.manager.listener

import com.kernel.finch.common.listeners.UpdateListener

internal class UpdateListenerManager : BaseListenerManager<UpdateListener>() {

    fun notifyListenersOnContentsChanged() = notifyListeners { it.onContentsChanged() }

    fun notifyListenersOnAllPendingChangesApplied() =
        notifyListeners { it.onAllPendingChangesApplied() }
}
