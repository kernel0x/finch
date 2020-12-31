package com.kernel.finch.core.util

import com.kernel.finch.FinchCore
import com.kernel.finch.components.special.NetworkLogs

internal fun formatTime(timestamp: Long) =
    timestamp.takeIf {
        it == 0L
    }?.let {
        ""
    } ?: FinchCore
        .implementation
        .find<NetworkLogs>(NetworkLogs.ID)
        ?.timeFormatter
        ?.invoke(
            timestamp
        ).toString()

internal fun formatDateTime(timestamp: Long) =
    timestamp.takeIf {
        it == 0L
    }?.let {
        ""
    } ?: FinchCore
        .implementation
        .find<NetworkLogs>(NetworkLogs.ID)
        ?.dateTimeFormatter
        ?.invoke(
            timestamp
        ).toString()

internal fun formatFileName(timestamp: Long) =
    timestamp.takeIf {
        it == 0L
    }?.let {
        ""
    } ?: FinchCore.implementation.configuration.logFileNameFormatter(timestamp)

