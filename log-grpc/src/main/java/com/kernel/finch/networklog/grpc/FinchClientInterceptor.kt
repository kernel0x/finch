package com.kernel.finch.networklog.grpc

import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.TextFormat
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
            val networkLog = NetworkLogEntity()
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                networkLog.requestDate = System.currentTimeMillis()
                networkLog.method = methodDescriptor.type.name
                networkLog.protocol = "h2"
                networkLog.setUrl(
                    url = "https://" + channel.authority() + "/" + methodDescriptor.fullMethodName,
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
                            networkLog.responseBody =
                                TextFormat.printer().escapingNonAscii(false).printToString(
                                    message as MessageOrBuilder
                                )
                            FinchGrpcLogger.logNetworkEvent(networkLog)
                            super.onMessage(message)
                        }

                        override fun onHeaders(headers: Metadata) {
                            networkLog.setResponseHeaders(toHttpHeaderList(headers))
                            FinchGrpcLogger.logNetworkEvent(networkLog)
                            super.onHeaders(headers)
                        }

                        override fun onClose(status: Status, trailers: Metadata) {
                            val duration =
                                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
                            networkLog.responseDate = System.currentTimeMillis()
                            networkLog.duration = duration
                            networkLog.responseCode = status.code.value()
                            networkLog.responseMessage = status.code.name
                            if (status.description?.isNotEmpty() == true) {
                                networkLog.responseMessage += " (" + status.description + ")"
                            }
                            networkLog.setResponseHeaders(toHttpHeaderList(trailers))
                            FinchGrpcLogger.logNetworkEvent(networkLog)
                            super.onClose(status, trailers)
                        }
                    },
                    headers
                )
            }

            override fun sendMessage(message: ReqT) {
                networkLog.requestBody = TextFormat.printer().escapingNonAscii(false).printToString(
                    message as MessageOrBuilder
                )
                FinchGrpcLogger.logNetworkEvent(networkLog)
                super.sendMessage(message)
            }
        }
    }

    private fun toHttpHeaderList(headers: Metadata): List<HeaderHttpModel> {
        val httpHeaders = ArrayList<HeaderHttpModel>()
        headers.keys().forEach { key ->
            if (!key.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
                runCatching {
                    httpHeaders.add(
                        HeaderHttpModel(
                            key,
                            headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))
                                ?: ""
                        )
                    )
                }
            }
        }
        return httpHeaders
    }
}
