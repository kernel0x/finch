package com.kernel.finch.core.manager

import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.components.special.NetworkLogs
import com.kernel.finch.core.data.db.NetworkLogDao
import com.kernel.finch.core.manager.listener.NetworkLogListenerManager

internal class NetworkLogManager(
    private val networkLogDao: NetworkLogDao,
    private val retentionManager: RetentionManager,
    private val networkLogListenerManager: NetworkLogListenerManager,
    private val listManager: ListManager,
    private val refreshUi: () -> Unit
) {

    private val entries = mutableMapOf<Long, NetworkLogEntity>()

    fun log(networkLog: NetworkLogEntity) {
        synchronized(entries) {
            networkLog.id = networkLogDao.insert(networkLog)
            entries[networkLog.id] = networkLog
            retentionManager.processing()
        }
        networkLogListenerManager.notifyListeners(networkLog)
        if (listManager.contains(NetworkLogs.ID)) {
            refreshUi()
        }
    }

    fun clearLogs() {
        networkLogDao.deleteAll()
        synchronized(entries) { entries.clear() }
        if (listManager.contains(NetworkLogs.ID)) {
            refreshUi()
        }
    }

    fun getEntries(): List<NetworkLogEntity> =
        synchronized(entries) { entries.map { it.value }.toList().sortedByDescending { it.id } }
}
