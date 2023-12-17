package com.kernel.finch.networklog.grpc

import com.kernel.finch.common.loggers.data.models.HeaderHttpModel
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import java.util.concurrent.TimeUnit

internal class FinchClientInterceptor : ClientInterceptor {

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        methodDescriptor: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions?,
        channel: Channel
    ): ClientCall<ReqT, RespT> {
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            channel.newCall(
                methodDescriptor,
                callOptions
            )
        ) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                val networkLog = NetworkLogEntity()
                networkLog.requestDate = System.currentTimeMillis()
                networkLog.method = methodDescriptor.type.name
                networkLog.setUrl(
                    url = "https://" + channel.authority() + methodDescriptor.fullMethodName,
                    host = channel.authority(),
                    path = methodDescriptor.fullMethodName,
                    scheme = "https"
                )

                networkLog.setRequestHeaders(toHttpHeaderList(headers))

                FinchGrpcLogger.logNetworkEvent(networkLog)

                val startNs = System.nanoTime()

                super.start(
                    object :
                        ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
                            responseListener
                        ) {
                        override fun onMessage(message: RespT) {
                            val duration =
                                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
                            networkLog.responseDate = System.currentTimeMillis()
                            networkLog.duration = duration
                            networkLog.protocol = "h2"
                            networkLog.responseBody = message.toString()
                            FinchGrpcLogger.logNetworkEvent(networkLog)
                            super.onMessage(message)
                        }

                        override fun onHeaders(headers: Metadata) {
                            networkLog.setResponseHeaders(toHttpHeaderList(headers))
                            FinchGrpcLogger.logNetworkEvent(networkLog)
                            super.onHeaders(headers)
                        }

                        override fun onClose(status: Status, trailers: Metadata) {
                            networkLog.responseCode = status.code.value()
                            networkLog.responseMessage = status.description ?: ""
                            networkLog.setResponseHeaders(toHttpHeaderList(trailers))
                            FinchGrpcLogger.logNetworkEvent(networkLog)
                            super.onClose(status, trailers)
                        }
                    },
                    headers
                )
            }
        }
    }

    private fun toHttpHeaderList(headers: Metadata): List<HeaderHttpModel> {
        val httpHeaders = ArrayList<HeaderHttpModel>()
        headers.keys().forEach { key ->
            httpHeaders.add(
                HeaderHttpModel(
                    key,
                    headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)) ?: ""
                )
            )
        }
        return httpHeaders
    }
}
