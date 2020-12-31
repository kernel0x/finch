package com.kernel.finch.implementation

import androidx.fragment.app.FragmentActivity
import com.kernel.finch.Finch
import com.kernel.finch.core.manager.UiManager

internal class DialogUiManager : UiManager {

    private val FragmentActivity.shouldShow
        get() = Finch.isUiEnabled && supportFragmentManager.findFragmentByTag(
            DebugMenuDialog.TAG
        ) as? DebugMenuDialog? == null
    private val FragmentActivity.dialogToHide
        get() = if (Finch.isUiEnabled) supportFragmentManager.findFragmentByTag(
            DebugMenuDialog.TAG
        ) as? DebugMenuDialog? else null

    override fun show(activity: FragmentActivity) = (activity.shouldShow).also { shouldShow ->
        if (shouldShow) {
            DebugMenuDialog.show(activity.supportFragmentManager)
        }
    }

    override fun hide(activity: FragmentActivity?) = (activity?.dialogToHide)?.let {
        it.dismiss()
        true
    } ?: false
}
