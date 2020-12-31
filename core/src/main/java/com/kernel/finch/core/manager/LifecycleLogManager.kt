package com.kernel.finch.core.manager

import com.kernel.finch.components.special.LifecycleLogs
import com.kernel.finch.core.util.LifecycleLogEntry

internal class LifecycleLogManager(
    private val listManager: ListManager,
    private val refreshUi: () -> Unit
) {
    private val entries = mutableListOf<LifecycleLogEntry>()

    fun log(
        classType: Class<*>,
        eventType: LifecycleLogs.EventType,
        hasSavedInstanceState: Boolean?
    ) {
        synchronized(entries) {
            entries.add(0, LifecycleLogEntry(classType, eventType, hasSavedInstanceState))
            entries.sortByDescending { it.date }
        }
        if (listManager.contains(LifecycleLogs.ID)) {
            refreshUi()
        }
    }

    fun getEntries(eventTypes: List<LifecycleLogs.EventType>): List<LifecycleLogEntry> =
        synchronized(entries) {
            entries.filter { eventTypes.contains(it.eventType) }.toList()
        }
}
