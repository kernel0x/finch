package com.kernel.finch.log.utils.extensions

import com.kernel.finch.log.FinchLogger

fun String.log() {
    FinchLogger.log(this)
}
