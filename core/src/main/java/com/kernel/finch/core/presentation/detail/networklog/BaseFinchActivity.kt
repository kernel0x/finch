package com.kernel.finch.core.presentation.detail.networklog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kernel.finch.core.manager.NotificationManager

internal abstract class BaseFinchActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager = NotificationManager(this)
    }

    override fun onResume() {
        super.onResume()
        isInForeground = true
        notificationManager.dismiss()
    }

    override fun onPause() {
        super.onPause()
        isInForeground = false
    }

    companion object {
        @kotlin.jvm.JvmStatic
        var isInForeground: Boolean = false
    }
}
