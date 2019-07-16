package com.kernel.finch.core.ui

import android.app.IntentService
import android.content.Intent
import com.kernel.finch.core.data.FinchDatabase

import com.kernel.finch.core.helpers.NotificationHelper

class ClearTransactionsService : IntentService("FinchClearTransactionsService") {
    override fun onHandleIntent(intent: Intent?) {
        FinchDatabase.getInstance(applicationContext).transactionHttp().deleteAll()
        NotificationHelper.clearBuffer()
        val notificationHelper = NotificationHelper(this)
        notificationHelper.dismiss()
    }
}