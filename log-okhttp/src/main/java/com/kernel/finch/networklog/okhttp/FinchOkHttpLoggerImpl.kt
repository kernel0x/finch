package com.kernel.finch.networklog.okhttp

import com.kernel.finch.common.loggers.FinchNetworkLogger
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import okhttp3.Interceptor

internal class FinchOkHttpLoggerImpl : FinchNetworkLogger {

    private var onNewLog: ((networkLog: NetworkLogEntity) -> Unit)? = null
    private var clearLogs: (() -> Unit)? = null
    override val logger: Interceptor by lazy { FinchInterceptor() }

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
