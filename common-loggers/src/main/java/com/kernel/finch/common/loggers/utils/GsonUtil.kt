package com.kernel.finch.common.loggers.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonUtil {

    private val gson: Gson by lazy {
        GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()
    }

    val instance: Gson
        get() {
            return gson.newBuilder()
                .serializeNulls()
                .create()
        }
}
