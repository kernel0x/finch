package com.kernel.finch.networklog.okhttp

import android.net.Uri
import com.kernel.finch.common.loggers.data.models.HeaderHttpModel
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import okio.BufferedSource
import okio.GzipSource
import okio.Okio
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.min

internal class FinchInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestBody = request.body()
        val hasRequestBody = requestBody != null

        val networkLog = NetworkLogEntity()

        networkLog.requestDate = System.currentTimeMillis()

        networkLog.method = request.method()

        val uri = Uri.parse(request.url().toString())
        networkLog.setUrl(
            url = request.url().toString(),
            host = uri.host.toString(),
            path = uri.path + if (uri.query != null) "?" + uri.query else "",
            scheme = uri.scheme.toString()
        )

        networkLog.setRequestHeaders(toHttpHeaderList(request.headers()))
        if (hasRequestBody) {
            if (requestBody.contentType() != null) {
                networkLog.requestContentType = requestBody.contentType().toString()
            }
            if (requestBody.contentLength() != -1L) {
                networkLog.requestContentLength = requestBody.contentLength()
            }
        }

        networkLog.requestBodyIsPlainText = bodyHasUnsupportedEncoding(request.headers())
        if (hasRequestBody && networkLog.requestBodyIsPlainText) {
            val source = getNativeSource(Buffer(), bodyGzipped(request.headers()))
            val buffer = source.buffer()
            requestBody.writeTo(buffer)
            var charset = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            if (isPlaintext(buffer)) {
                networkLog.requestBody = readFromBuffer(buffer, charset)
            } else {
                networkLog.responseBodyIsPlainText = false
            }
        }

        FinchOkHttpLogger.logNetworkEvent(networkLog)

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            networkLog.error = e.toString()
            FinchOkHttpLogger.logNetworkEvent(networkLog)
            throw e
        }

        val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()

        networkLog.setRequestHeaders(toHttpHeaderList(response.request().headers()))
        networkLog.responseDate = System.currentTimeMillis()
        networkLog.duration = duration
        networkLog.protocol = response.protocol().toString()
        networkLog.responseCode = response.code()
        networkLog.responseMessage = response.message()

        networkLog.responseContentLength = responseBody.contentLength()
        if (responseBody.contentType() != null) {
            networkLog.responseContentType = responseBody.contentType().toString()
        }
        networkLog.setResponseHeaders(toHttpHeaderList(response.headers()))

        networkLog.responseBodyIsPlainText = bodyHasUnsupportedEncoding(response.headers())
        if (HttpHeaders.hasBody(response) && networkLog.responseBodyIsPlainText) {
            val source = getNativeSource(response)
            source.request(java.lang.Long.MAX_VALUE)
            val buffer = source.buffer()
            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8)
                } catch (e: UnsupportedCharsetException) {
                    FinchOkHttpLogger.logNetworkEvent(networkLog)
                    return response
                }

            }
            if (isPlaintext(buffer)) {
                networkLog.responseBody = readFromBuffer(buffer.clone(), charset)
            } else {
                networkLog.responseBodyIsPlainText = false
            }
            networkLog.responseContentLength = buffer.size()
        }

        FinchOkHttpLogger.logNetworkEvent(networkLog)

        return response
    }

    private fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = if (buffer.size() < 64) buffer.size() else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (e: EOFException) {
            return false
        }
    }

    private fun bodyHasUnsupportedEncoding(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding == null ||
            contentEncoding.equals("identity", ignoreCase = true) ||
            contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun bodyGzipped(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return "gzip".equals(contentEncoding, ignoreCase = true)
    }

    private fun readFromBuffer(buffer: Buffer, charset: Charset): String {
        val bufferSize = buffer.size()
        val maxBytes = min(bufferSize, maxContentLength)
        var body = ""
        try {
            body = buffer.readString(maxBytes, charset)
        } catch (e: EOFException) {
            body += "\\n\\n--- Unexpected end of content ---"
        }

        if (bufferSize > maxContentLength) {
            body += "\\n\\n--- Content truncated ---"
        }
        return body
    }

    private fun getNativeSource(input: BufferedSource, isGzipped: Boolean): BufferedSource {
        return if (isGzipped) {
            val source = GzipSource(input)
            Okio.buffer(source)
        } else {
            input
        }
    }

    @Throws(IOException::class)
    private fun getNativeSource(response: Response): BufferedSource {
        if (bodyGzipped(response.headers())) {
            val source = response.peekBody(maxContentLength).source()
            if (source.buffer().size() < maxContentLength) {
                return getNativeSource(source, true)
            }
        }
        return response.body().source()
    }

    private fun toHttpHeaderList(headers: Headers): List<HeaderHttpModel> {
        val httpHeaders = ArrayList<HeaderHttpModel>()
        var i = 0
        val count = headers.size()
        while (i < count) {
            httpHeaders.add(HeaderHttpModel(headers.name(i), headers.value(i)))
            i++
        }
        return httpHeaders
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
        private const val maxContentLength = 250000L
    }
}
