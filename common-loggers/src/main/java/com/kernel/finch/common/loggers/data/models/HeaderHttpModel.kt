package com.kernel.finch.common.loggers.data.models

import androidx.annotation.Keep

@Keep
data class HeaderHttpModel(
    val name: String,
    val value: String
)
