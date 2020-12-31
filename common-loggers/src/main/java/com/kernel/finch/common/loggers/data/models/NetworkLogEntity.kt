package com.kernel.finch.common.loggers.data.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.reflect.TypeToken
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity.Companion.TABLE_NAME
import com.kernel.finch.common.loggers.utils.FormatUtil
import com.kernel.finch.common.loggers.utils.GsonUtil
import java.util.*

@Suppress("TooManyFunctions")
@Keep
@Entity(tableName = TABLE_NAME)
data class NetworkLogEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var protocol: String = "",
    var method: String = "",
    var url: String = "",
    var host: String = "",
    var path: String = "",
    var scheme: String = "",
    var duration: Long = 0,
    var requestDate: Long = 0,
    var requestBody: String = "",
    var requestContentLength: Long = 0,
    var requestContentType: String = "",
    var requestHeaders: String = "",
    var requestBodyIsPlainText: Boolean = true,
    var responseDate: Long = 0,
    var responseBody: String = "",
    var responseContentLength: Long = 0,
    var responseContentType: String = "",
    var responseCode: Int = 0,
    var responseMessage: String = "",
    var responseHeaders: String = "",
    var responseBodyIsPlainText: Boolean = true,
    var error: String? = null
) {

    fun setUrl(url: String, host: String, path: String, scheme: String) {
        this.url = url
        this.host = host
        this.path = path
        this.scheme = scheme
    }

    fun getFormattedRequestBody(): String {
        return formatBody(requestBody, requestContentType)
    }

    fun getFormattedResponseBody(): String {
        return formatBody(responseBody, responseContentType)
    }

    fun setRequestHeaders(headers: List<HeaderHttpModel>) {
        requestHeaders = GsonUtil.instance.toJson(headers)
    }

    fun getRequestHeadersAsList(): List<HeaderHttpModel>? {
        return GsonUtil.instance.fromJson<List<HeaderHttpModel>>(
            requestHeaders,
            object : TypeToken<List<HeaderHttpModel>>() {
            }.type
        )
    }

    fun getRequestHeadersString(withMarkup: Boolean): String {
        return FormatUtil.formatHeaders(getRequestHeadersAsList(), withMarkup)
    }

    fun setResponseHeaders(headers: List<HeaderHttpModel>) {
        responseHeaders = GsonUtil.instance.toJson(headers)
    }

    fun getResponseHeadersAsList(): List<HeaderHttpModel>? {
        return GsonUtil.instance.fromJson<List<HeaderHttpModel>>(
            responseHeaders,
            object : TypeToken<List<HeaderHttpModel>>() {
            }.type
        )
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
        return scheme.toLowerCase(Locale.getDefault()) == "https"
    }

    private fun formatBody(body: String, contentType: String?): String {
        return if (contentType != null && contentType.toLowerCase(Locale.getDefault())
                .contains("json")
        ) {
            FormatUtil.formatJson(body)
        } else if (contentType != null && contentType.toLowerCase(Locale.getDefault())
                .contains("xml")
        ) {
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
        const val TABLE_NAME = "network_log"
    }
}
