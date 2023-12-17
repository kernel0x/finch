package com.kernel.finch.networklog.grpc

import com.kernel.finch.common.loggers.FinchNetworkLogger
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import io.grpc.ClientInterceptor

internal class FinchGrpcLoggerImpl : FinchNetworkLogger {

    private var onNewLog: ((networkLog: NetworkLogEntity) -> Unit)? = null
    private var clearLogs: (() -> Unit)? = null
    override val logger: ClientInterceptor by lazy { FinchClientInterceptor() }

    override fun logNetworkEvent(networkLog: NetworkLogEntity) {
        onNewLog?.invoke(networkLog)
    }

    override fun clearNetworkLogs() {
        clearLogs?.invoke()
    }

    override fun register(
        onNewLog: (networkLog: NetworkLogEntity) -> Unit,
        clearLogs: () -> Unit
    ) {
        this.onNewLog = onNewLog
        this.clearLogs = clearLogs
    }
}
