package com.kernel.finch.implementation

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.kernel.finch.Finch
import com.kernel.finch.core.manager.UiManager

internal class ActivityUiManager : UiManager {

    private val FragmentActivity.shouldShow get() = Finch.isUiEnabled && this !is DebugMenuActivity
    private val FragmentActivity.shouldHide get() = Finch.isUiEnabled && this is DebugMenuActivity
    var debugMenuActivity: DebugMenuActivity? = null

    override fun show(activity: FragmentActivity) = (activity.shouldShow).also { shouldShow ->
        if (shouldShow) {
            activity.startActivity(Intent(activity, DebugMenuActivity::class.java))
        }
    }

    override fun hide(activity: FragmentActivity?) =
        (debugMenuActivity?.shouldHide == true).also { shouldHide ->
            if (shouldHide) {
                debugMenuActivity?.supportFinishAfterTransition()
            }
        }

    override fun findHostFragmentManager() =
        debugMenuActivity?.supportFragmentManager ?: super.findHostFragmentManager()
}
