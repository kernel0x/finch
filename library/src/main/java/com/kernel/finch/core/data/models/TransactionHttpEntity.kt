package com.kernel.finch.core.data.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.reflect.TypeToken
import com.kernel.finch.core.data.models.TransactionHttpEntity.Companion.TABLE_NAME
import com.kernel.finch.core.utils.FormatUtil
import com.kernel.finch.core.utils.GsonUtil
import okhttp3.Headers
import okhttp3.HttpUrl
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = TABLE_NAME)
data class TransactionHttpEntity(
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        var duration: Long = 0,
        var protocol: String = "",
        var method: String = "",
        var url: String = "",
        var host: String = "",
        var path: String = "",
        var scheme: String = "",
        var requestDate: String = "",
        var requestBody: String = "",
        var requestContentLength: Long = 0,
        var requestContentType: String = "",
        var requestHeaders: String = "",
        var requestBodyIsPlainText: Boolean = true,
        var responseDate: String = "",
        var responseBody: String = "",
        var responseContentLength: Long = 0,
        var responseContentType: String = "",
        var responseCode: Int = 0,
        var responseMessage: String = "",
        var responseHeaders: String = "",
        var responseBodyIsPlainText: Boolean = true,
        var error: String? = null
) {

    fun setUrl(url: HttpUrl) {
        this.url = url.toString()
        val uri = Uri.parse(url.toString())
        host = uri.host.toString()
        path = uri.path!! + if (uri.query != null) "?" + uri.query!! else ""
        scheme = uri.scheme.toString()
    }

    fun setRequestDate(requestDate: Date) {
        this.requestDate = FORMAT_DATE.format(requestDate)
    }

    fun setResponseDate(responseDate: Date) {
        this.responseDate = FORMAT_DATE.format(responseDate)
    }

    fun getFormattedRequestBody(): String {
        return formatBody(requestBody, requestContentType)
    }

    fun getFormattedResponseBody(): String {
        return formatBody(responseBody, responseContentType)
    }

    fun setRequestHeaders(headers: Headers) {
        setRequestHeaders(toHttpHeaderList(headers))
    }

    private fun setRequestHeaders(headers: List<HeaderHttpModel>) {
        requestHeaders = GsonUtil.instance.toJson(headers)
    }

    fun getRequestHeadersAsList(): List<HeaderHttpModel>? {
        return GsonUtil.instance.fromJson<List<HeaderHttpModel>>(requestHeaders,
                object : TypeToken<List<HeaderHttpModel>>() {

                }.type)
    }

    fun getRequestHeadersString(withMarkup: Boolean): String {
        return FormatUtil.formatHeaders(getRequestHeadersAsList(), withMarkup)
    }

    fun setResponseHeaders(headers: Headers) {
        setResponseHeaders(toHttpHeaderList(headers))
    }

    private fun setResponseHeaders(headers: List<HeaderHttpModel>) {
        responseHeaders = GsonUtil.instance.toJson(headers)
    }

    fun getResponseHeadersAsList(): List<HeaderHttpModel>? {
        return GsonUtil.instance.fromJson<List<HeaderHttpModel>>(responseHeaders,
                object : TypeToken<List<HeaderHttpModel>>() {

                }.type)
    }

    fun getResponseHeadersString(withMarkup: Boolean): String {
        return FormatUtil.formatHeaders(getResponseHeadersAsList(), withMarkup)
    }

    fun getStatus(): Status {
        return when {
            error != null -> Status.FAIL
            responseCode == 0 -> Status.PROGRESS
            else -> Status.COMPLETE
        }
    }

    fun getDurationString(): String? {
        return "$duration ms"
    }

    fun getRequestSizeString(): String {
        return formatBytes(requestContentLength)
    }

    fun getResponseSizeString(): String? {
        return formatBytes(responseContentLength)
    }

    fun getTotalSizeString(): String {
        return formatBytes(requestContentLength + responseContentLength)
    }

    fun getResponseSummaryText(): String? {
        return when (getStatus()) {
            Status.FAIL -> error
            Status.PROGRESS -> null
            else -> "$responseCode $responseMessage"
        }
    }

    fun getNotificationText(): String {
        return when (getStatus()) {
            Status.FAIL -> " ! ! !  $path"
            Status.PROGRESS -> " . . .  $path"
            else -> "$responseCode $path"
        }
    }

    fun isSsl(): Boolean {
        return scheme.toLowerCase() == "https"
    }

    fun getRequestStartTimeString(): String? {
        try {
            val date = FORMAT_DATE.parse(requestDate)
            return if (date != null) FORMAT_TIME.format(date) else null
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
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

    private fun formatBody(body: String, contentType: String?): String {
        return if (contentType != null && contentType.toLowerCase().contains("json")) {
            FormatUtil.formatJson(body)
        } else if (contentType != null && contentType.toLowerCase().contains("xml")) {
            FormatUtil.formatXml(body)
        } else {
            body
        }
    }

    private fun formatBytes(bytes: Long): String {
        return FormatUtil.formatByteCount(bytes, true)
    }

    enum class Status {
        COMPLETE,
        FAIL,
        PROGRESS
    }

    companion object {
        const val TABLE_NAME = "transactionhttp"

        private val FORMAT_TIME = SimpleDateFormat("HH:mm:ss", Locale.US)
        private val FORMAT_DATE = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    }
}