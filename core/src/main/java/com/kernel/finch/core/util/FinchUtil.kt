package com.kernel.finch.core.util

import android.content.Context
import android.content.Intent
import com.kernel.finch.core.presentation.detail.networklog.ContainerActivity

internal object FinchUtil {

    @JvmStatic
    fun getLaunchIntent(context: Context): Intent {
        return Intent(
            context,
            ContainerActivity::class.java
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
