package com.kernel.finch.common.loggers.utils

import com.google.gson.JsonParser
import com.kernel.finch.common.loggers.data.models.HeaderHttpModel
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
        return String.format(
            Locale.US,
            "%.1f %sB",
            bytes / unit.toDouble().pow(exp.toDouble()),
            pre
        )
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
}
