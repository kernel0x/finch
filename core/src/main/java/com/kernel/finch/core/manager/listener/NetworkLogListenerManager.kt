package com.kernel.finch.core.manager.listener

import com.kernel.finch.common.listeners.NetworkLogListener
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity

internal class NetworkLogListenerManager : BaseListenerManager<NetworkLogListener>() {

    fun notifyListeners(networkLog: NetworkLogEntity) = notifyListeners {
        it.onAdded(
            networkLog
        )
    }
}
