package com.kernel.finch.common.listeners

interface LogListener {
    fun onAdded(tag: String?, message: CharSequence, payload: CharSequence?)
}
