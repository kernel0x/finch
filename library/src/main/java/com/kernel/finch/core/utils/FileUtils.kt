package com.kernel.finch.core.utils

import android.content.Context
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.widget.Toast
import java.io.File
import java.io.FileWriter
import java.io.IOException


object FileUtils {

    private const val EXTENSION_TEXT = ".txt"
    private const val EXTENSION_JSON = ".json"

    fun createJsonFile(context: Context, fileName: String, data: String): File? =
            createFile(context, fileName, EXTENSION_JSON, data)

    fun createTextFile(context: Context, fileName: String, data: String): File? =
            createFile(context, fileName, EXTENSION_TEXT, data)

    private fun createFile(context: Context, fileName: String, extension: String, data: String): File? {
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