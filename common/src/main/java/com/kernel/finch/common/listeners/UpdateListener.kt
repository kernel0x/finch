package com.kernel.finch.common.listeners

interface UpdateListener {
    fun onContentsChanged()
    fun onAllPendingChangesApplied() = Unit
}
