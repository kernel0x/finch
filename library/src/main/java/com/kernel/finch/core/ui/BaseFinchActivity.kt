package com.kernel.finch.core.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.kernel.finch.core.helpers.NotificationHelper

abstract class BaseFinchActivity : AppCompatActivity() {

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationHelper = NotificationHelper(this)
    }

    override fun onResume() {
        super.onResume()
        isInForeground = true
        notificationHelper.dismiss()
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
