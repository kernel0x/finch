package com.kernel.finch.core.utils

import android.content.Context
import android.text.TextUtils
import com.google.gson.JsonParser
import com.kernel.finch.R
import com.kernel.finch.core.data.models.HeaderHttpModel
import com.kernel.finch.core.data.models.TransactionHttpEntity
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.sax.SAXTransformerFactory
import javax.xml.transform.stream.StreamResult
import kotlin.math.ln
import kotlin.math.pow

object FormatUtil {

    fun formatHeaders(httpHeaders: List<HeaderHttpModel>?, withMarkup: Boolean): String {
        var out = ""
        if (httpHeaders != null) {
            for ((name, value) in httpHeaders) {
                out += (if (withMarkup) "<b>" else "") + name + ": " + (if (withMarkup) "</b>" else "") +
                        value + if (withMarkup) "<br />" else "\n"
            }
        }
        return out
    }

    fun formatByteCount(bytes: Long, si: Boolean): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return "$bytes B"
        val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
        return String.format(Locale.US, "%.1f %sB", bytes / unit.toDouble().pow(exp.toDouble()), pre)
    }

    fun formatJson(json: String): String {
        return try {
            val jp = JsonParser()
            val je = jp.parse(json)
            GsonUtil.instance.toJson(je)
        } catch (e: Exception) {
            json
        }
    }

    fun formatXml(xml: String): String {
        return try {
            val serializer = SAXTransformerFactory.newInstance().newTransformer()
            serializer.setOutputProperty(OutputKeys.INDENT, "yes")
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            val xmlSource = SAXSource(InputSource(ByteArrayInputStream(xml.toByteArray())))
            val res = StreamResult(ByteArrayOutputStream())
            serializer.transform(xmlSource, res)
            String((res.outputStream as ByteArrayOutputStream).toByteArray())
        } catch (e: Exception) {
            xml
        }
    }

    fun getShareText(context: Context, transaction: TransactionHttpEntity): String {
        var text = ""
        text += context.getString(R.string.lib_finch_url) + ": " + v(transaction.url) + "\n"
        text += context.getString(R.string.lib_finch_method) + ": " + v(transaction.method) + "\n"
        text += context.getString(R.string.lib_finch_protocol) + ": " + v(transaction.protocol) + "\n"
        text += context.getString(R.string.lib_finch_status) + ": " + v(transaction.getStatus().toString()) + "\n"
        text += context.getString(R.string.lib_finch_response) + ": " + v(transaction.getResponseSummaryText()) + "\n"
        text += context.getString(R.string.lib_finch_ssl) + ": " + v(context.getString(if (transaction.isSsl()) R.string.lib_finch_yes else R.string.lib_finch_no)) + "\n"
        text += "\n"
        text += context.getString(R.string.lib_finch_request_time) + ": " + v(transaction.requestDate) + "\n"
        text += context.getString(R.string.lib_finch_response_time) + ": " + v(transaction.responseDate) + "\n"
        text += context.getString(R.string.lib_finch_duration) + ": " + v(transaction.getDurationString()) + "\n"
        text += "\n"
        text += context.getString(R.string.lib_finch_request_size) + ": " + v(transaction.getRequestSizeString()) + "\n"
        text += context.getString(R.string.lib_finch_response_size) + ": " + v(transaction.getResponseSizeString()) + "\n"
        text += context.getString(R.string.lib_finch_total_size) + ": " + v(transaction.getTotalSizeString()) + "\n"
        text += "\n"
        text += "---------- " + context.getString(R.string.lib_finch_request).toUpperCase() + " ----------\n\n"
        var headers = formatHeaders(transaction.getRequestHeadersAsList(), false)
        if (!TextUtils.isEmpty(headers)) {
            text += headers + "\n"
        }
        text += if (transaction.requestBodyIsPlainText)
            v(transaction.getFormattedRequestBody())
        else
            context.getString(R.string.lib_finch_body_omitted)
        text += "\n\n"
        text += "---------- " + context.getString(R.string.lib_finch_response).toUpperCase() + " ----------\n\n"
        headers = formatHeaders(transaction.getResponseHeadersAsList(), false)
        if (!TextUtils.isEmpty(headers)) {
            text += headers + "\n"
        }
        text += if (transaction.responseBodyIsPlainText)
            v(transaction.getFormattedResponseBody())
        else
            context.getString(R.string.lib_finch_body_omitted)
        return text
    }

    fun getShareCurlCommand(transaction: TransactionHttpEntity): String {
        var compressed = false
        var curlCmd = "curl"
        curlCmd += " -X " + transaction.method
        val headers = transaction.getRequestHeadersAsList()
        var i = 0
        val count = headers!!.size
        while (i < count) {
            val name = headers[i].name
            val value = headers[i].value
            if ("Accept-Encoding".equals(name, ignoreCase = true) && "gzip".equals(value, ignoreCase = true)) {
                compressed = true
            }
            curlCmd += " -H \"$name: $value\""
            i++
        }
        val requestBody = transaction.requestBody
        if (requestBody.isNotEmpty()) {
            curlCmd += " --data $'" + requestBody.replace("\n", "\\n") + "'"
        }
        curlCmd += (if (compressed) " --compressed " else " ") + transaction.url
        return curlCmd
    }

    private fun v(string: String?): String {
        return string ?: ""
    }
}