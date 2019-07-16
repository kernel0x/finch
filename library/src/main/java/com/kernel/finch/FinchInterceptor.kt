package com.kernel.finch

import android.content.Context
import android.util.Log
import com.kernel.finch.core.data.FinchDatabase
import com.kernel.finch.core.data.models.TransactionHttpEntity
import com.kernel.finch.core.helpers.NotificationHelper
import com.kernel.finch.core.helpers.RetentionHelper
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

class FinchInterceptor(context: Context) : Interceptor {

    private val context: Context = context.applicationContext
    private val notificationHelper: NotificationHelper
    private var retentionManager: RetentionHelper? = null
    private var isShowNotification: Boolean = false
    private var maxContentLength = 250000L

    enum class Period {
        ONE_HOUR,
        ONE_DAY,
        ONE_WEEK,
        FOREVER
    }

    init {
        notificationHelper = NotificationHelper(this.context)
        isShowNotification = true
        retentionManager = RetentionHelper(this.context, DEFAULT_RETENTION)
    }

    fun setShowNotification(isShow: Boolean): FinchInterceptor {
        isShowNotification = isShow
        return this
    }

    fun setMaxContentLength(max: Long): FinchInterceptor {
        this.maxContentLength = max
        return this
    }

    fun setRetentionPeriod(period: Period): FinchInterceptor {
        retentionManager = RetentionHelper(context, period)
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestBody = request.body()
        val hasRequestBody = requestBody != null

        val transaction = TransactionHttpEntity()
        transaction.setRequestDate(Date())

        transaction.method = request.method()
        transaction.setUrl(request.url())

        transaction.setRequestHeaders(request.headers())
        if (hasRequestBody) {
            if (requestBody!!.contentType() != null) {
                transaction.requestContentType = requestBody.contentType().toString()
            }
            if (requestBody.contentLength() != -1L) {
                transaction.requestContentLength = requestBody.contentLength()
            }
        }

        transaction.requestBodyIsPlainText = bodyHasUnsupportedEncoding(request.headers())
        if (hasRequestBody && transaction.requestBodyIsPlainText) {
            val source = getNativeSource(Buffer(), bodyGzipped(request.headers()))
            val buffer = source.buffer()
            requestBody!!.writeTo(buffer)
            var charset = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            if (isPlaintext(buffer)) {
                transaction.requestBody = readFromBuffer(buffer, charset)
            } else {
                transaction.responseBodyIsPlainText = false
            }
        }

        create(transaction)

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            transaction.error = e.toString()
            update(transaction)
            throw e
        }

        val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()

        transaction.setRequestHeaders(response.request().headers())
        transaction.setResponseDate(Date())
        transaction.duration = duration
        transaction.protocol = response.protocol().toString()
        transaction.responseCode = response.code()
        transaction.responseMessage = response.message()

        transaction.responseContentLength = responseBody.contentLength()
        if (responseBody.contentType() != null) {
            transaction.responseContentType = responseBody.contentType().toString()
        }
        transaction.setResponseHeaders(response.headers())

        transaction.responseBodyIsPlainText = bodyHasUnsupportedEncoding(response.headers())
        if (HttpHeaders.hasBody(response) && transaction.responseBodyIsPlainText) {
            val source = getNativeSource(response)
            source.request(java.lang.Long.MAX_VALUE)
            val buffer = source.buffer()
            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8)
                } catch (e: UnsupportedCharsetException) {
                    update(transaction)
                    return response
                }

            }
            if (isPlaintext(buffer)) {
                transaction.responseBody = readFromBuffer(buffer.clone(), charset)
            } else {
                transaction.responseBodyIsPlainText = false
            }
            transaction.responseContentLength = buffer.size()
        }

        update(transaction)

        return response
    }

    private fun create(transaction: TransactionHttpEntity) {
        transaction.id = FinchDatabase.getInstance(context).transactionHttp().insert(transaction)
        if (isShowNotification) {
            notificationHelper.show(transaction)
        }
        retentionManager!!.processing()
    }

    private fun update(transaction: TransactionHttpEntity): Int {
        val updated = FinchDatabase.getInstance(context).transactionHttp().update(transaction)
        if (isShowNotification && updated > 0) {
            notificationHelper.show(transaction)
        }
        return updated
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
            body += context.getString(R.string.lib_finch_body_unexpected_eof)
        }

        if (bufferSize > maxContentLength) {
            body += context.getString(R.string.lib_finch_body_content_truncated)
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
            } else {
                Log.w(TAG, "gzip encoded response was too long")
            }
        }
        return response.body().source()
    }

    companion object {
        private const val TAG = "FinchInterceptor"
        private val UTF8 = Charset.forName("UTF-8")
        private val DEFAULT_RETENTION = Period.ONE_WEEK
    }
}
