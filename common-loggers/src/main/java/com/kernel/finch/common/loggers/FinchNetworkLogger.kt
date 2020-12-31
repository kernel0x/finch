package com.kernel.finch.common.loggers

import com.kernel.finch.common.loggers.data.models.NetworkLogEntity

interface FinchNetworkLogger {

    val logger: Any? get() = null

    fun logNetworkEvent(networkLog: NetworkLogEntity) = Unit

    fun clearNetworkLogs() = Unit

    fun register(
        onNewLog: (networkLog: NetworkLogEntity) -> Unit,
        clearLogs: () -> Unit
    ) = Unit
}
