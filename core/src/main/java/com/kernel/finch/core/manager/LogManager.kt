package com.kernel.finch.core.manager

import com.kernel.finch.components.special.Logs
import com.kernel.finch.core.manager.listener.LogListenerManager
import com.kernel.finch.core.util.LogEntry

internal class LogManager(
    private val logListenerManager: LogListenerManager,
    private val listManager: ListManager,
    private val refreshUi: () -> Unit
) {
    private val entries = mutableListOf<LogEntry>()

    fun log(label: String?, message: CharSequence, payload: CharSequence?) {
        synchronized(entries) {
            entries.add(
                0,
                LogEntry(
                    label = label,
                    message = message,
                    payload = payload
                )
            )
            entries.sortByDescending { it.date }
        }
        logListenerManager.notifyListeners(label, message, payload)
        refreshUiIfNeeded(label)
    }

    fun clearLogs(label: String?) {
        synchronized(entries) {
            if (label == null) {
                entries.clear()
            } else {
                entries.removeAll { it.label == label }
            }
        }
        refreshUiIfNeeded(label)
    }

    fun getEntries(label: String?): List<LogEntry> = synchronized(entries) {
        if (label == null) {
            entries.toList()
        } else {
            entries.filter { it.label == label }.toList()
        }
    }

    private fun refreshUiIfNeeded(label: String?) {
        if (listManager.contains(Logs.formatId(null)) || listManager.contains(
                Logs.formatId(
                    label
                )
            )
        ) {
            refreshUi()
        }
    }
}
