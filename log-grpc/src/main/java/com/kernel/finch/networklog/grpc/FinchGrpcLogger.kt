package com.kernel.finch.networklog.grpc

import com.kernel.finch.common.loggers.FinchNetworkLogger

object FinchGrpcLogger : FinchNetworkLogger by FinchGrpcLoggerImpl()
