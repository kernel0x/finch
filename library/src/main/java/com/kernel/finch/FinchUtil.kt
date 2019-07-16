package com.kernel.finch

import android.content.Context
import android.content.Intent

import com.kernel.finch.core.ui.MainActivity

object FinchUtil {
    /**
     * Get Intent to launch UI Finch
     */
    @JvmStatic
    fun getLaunchIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}