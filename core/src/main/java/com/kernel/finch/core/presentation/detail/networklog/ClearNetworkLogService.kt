package com.kernel.finch.core.presentation.detail.networklog

import android.app.IntentService
import android.content.Intent
import com.kernel.finch.core.data.db.FinchDatabase
import com.kernel.finch.core.manager.NotificationManager

internal class ClearNetworkLogService : IntentService("FinchClearNetworkLogService") {

    override fun onHandleIntent(intent: Intent?) {
        FinchDatabase.getInstance(applicationContext).networkLog().deleteAll()
        NotificationManager.clearBuffer()
        val notificationHelper = NotificationManager(this)
        notificationHelper.dismiss()
    }
}
