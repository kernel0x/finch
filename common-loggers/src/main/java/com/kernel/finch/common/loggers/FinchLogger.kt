package com.kernel.finch.common.loggers

interface FinchLogger {

    fun log(message: CharSequence, label: String? = null, payload: CharSequence? = null) = Unit

    fun clearLogs(label: String? = null) = Unit

    fun register(
        onNewLog: (message: CharSequence, label: String?, payload: CharSequence?) -> Unit,
        clearLogs: (label: String?) -> Unit
    ) = Unit
}
