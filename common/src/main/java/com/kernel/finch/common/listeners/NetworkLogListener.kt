package com.kernel.finch.common.listeners

import com.kernel.finch.common.loggers.data.models.NetworkLogEntity

interface NetworkLogListener {
    fun onAdded(networkLog: NetworkLogEntity)
}
