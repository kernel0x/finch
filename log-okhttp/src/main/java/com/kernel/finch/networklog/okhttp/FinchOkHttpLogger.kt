package com.kernel.finch.networklog.okhttp

import com.kernel.finch.common.loggers.FinchNetworkLogger

object FinchOkHttpLogger : FinchNetworkLogger by FinchOkHttpLoggerImpl()
