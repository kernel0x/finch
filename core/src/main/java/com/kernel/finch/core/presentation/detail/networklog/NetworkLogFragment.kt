package com.kernel.finch.core.presentation.detail.networklog

import com.kernel.finch.common.loggers.data.models.NetworkLogEntity

internal interface NetworkLogFragment {
    fun networkLogUpdated(networkLog: NetworkLogEntity)
}
