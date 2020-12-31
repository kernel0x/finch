package com.kernel.finch.core.util

import android.content.Context
import android.text.TextUtils
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.common.loggers.utils.FormatUtil.formatHeaders
import com.kernel.finch.core.R
import java.util.*

object Text {

    fun getShareText(context: Context, networkLog: NetworkLogEntity): String {
        var text = ""
        text += context.getString(R.string.finch_url) + ": " + v(networkLog.url) + "\n"
        text += context.getString(R.string.finch_method) + ": " + v(networkLog.method) + "\n"
        text += context.getString(R.string.finch_protocol) + ": " + v(networkLog.protocol) + "\n"
        text += context.getString(R.string.finch_status) + ": " + v(
            networkLog.getStatus().toString()
        ) + "\n"
        text += context.getString(R.string.finch_response) + ": " + v(networkLog.getResponseSummaryText()) + "\n"
        text += context.getString(R.string.finch_ssl) + ": " + v(context.getString(if (networkLog.isSsl()) R.string.finch_yes else R.string.finch_no)) + "\n"
        text += "\n"
        text += context.getString(R.string.finch_request_time) + ": " + v(formatDateTime(networkLog.requestDate)) + "\n"
        text += context.getString(R.string.finch_response_time) + ": " + v(formatDateTime(networkLog.responseDate)) + "\n"
        text += context.getString(R.string.finch_duration) + ": " + v(networkLog.getDurationString()) + "\n"
        text += "\n"
        text += context.getString(R.string.finch_request_size) + ": " + v(networkLog.getRequestSizeString()) + "\n"
        text += context.getString(R.string.finch_response_size) + ": " + v(networkLog.getResponseSizeString()) + "\n"
        text += context.getString(R.string.finch_total_size) + ": " + v(networkLog.getTotalSizeString()) + "\n"
        text += "\n"
        text += "---------- " + context.getString(R.string.finch_request)
            .toUpperCase(Locale.getDefault()) + " ----------\n\n"
        var headers = formatHeaders(networkLog.getRequestHeadersAsList(), false)
        if (!TextUtils.isEmpty(headers)) {
            text += headers + "\n"
        }
        text += if (networkLog.requestBodyIsPlainText)
            v(networkLog.getFormattedRequestBody())
        else
            context.getString(R.string.finch_body_omitted)
        text += "\n\n"
        text += "---------- " + context.getString(R.string.finch_response)
            .toUpperCase(Locale.getDefault()) + " ----------\n\n"
        headers = formatHeaders(networkLog.getResponseHeadersAsList(), false)
        if (!TextUtils.isEmpty(headers)) {
            text += headers + "\n"
        }
        text += if (networkLog.responseBodyIsPlainText)
            v(networkLog.getFormattedResponseBody())
        else
            context.getString(R.string.finch_body_omitted)
        return text
    }

    fun getShareCurlCommand(networkLog: NetworkLogEntity): String {
        var compressed = false
        var curlCmd = "curl"
        curlCmd += " -X " + networkLog.method
        val headers = networkLog.getRequestHeadersAsList()
        var i = 0
        val count = headers!!.size
        while (i < count) {
            val name = headers[i].name
            val value = headers[i].value
            if ("Accept-Encoding".equals(name, ignoreCase = true) && "gzip".equals(
                    value,
                    ignoreCase = true
                )
            ) {
                compressed = true
            }
            curlCmd += " -H \"$name: $value\""
            i++
        }
        val requestBody = networkLog.requestBody
        if (requestBody.isNotEmpty()) {
            curlCmd += " --data $'" + requestBody.replace("\n", "\\n") + "'"
        }
        curlCmd += (if (compressed) " --compressed " else " ") + networkLog.url
        return curlCmd
    }

    private fun v(string: String?): String {
        return string ?: ""
    }
}
