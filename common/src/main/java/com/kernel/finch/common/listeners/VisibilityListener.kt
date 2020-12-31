package com.kernel.finch.common.listeners

interface VisibilityListener {
    fun onShown() = Unit
    fun onHidden() = Unit
}
