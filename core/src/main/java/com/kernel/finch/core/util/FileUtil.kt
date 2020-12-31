package com.kernel.finch.core.util

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.IOException

internal object FileUtil {

    private const val EXTENSION_TEXT = ".txt"
    private const val EXTENSION_JSON = ".json"

    fun createJsonFile(context: Context, fileName: CharSequence, data: CharSequence): File? =
        createFile(context, fileName, EXTENSION_JSON, data)

    fun createTextFile(context: Context, fileName: CharSequence, data: CharSequence): File? =
        createFile(context, fileName, EXTENSION_TEXT, data)

    private fun createFile(
        context: Context,
        fileName: CharSequence,
        extension: CharSequence,
        data: CharSequence
    ): File? {
        try {
            val root = File(context.cacheDir.absolutePath)
            if (!root.exists()) {
                root.mkdirs()
            }
            val file = File(root, "$fileName$extension")

            val writer = FileWriter(file, true)
            writer.append(data)
            writer.flush()
            writer.close()
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
