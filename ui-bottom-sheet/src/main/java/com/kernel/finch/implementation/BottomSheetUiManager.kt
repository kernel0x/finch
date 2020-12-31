package com.kernel.finch.implementation

import androidx.fragment.app.FragmentActivity
import com.kernel.finch.Finch
import com.kernel.finch.core.manager.UiManager

internal class BottomSheetUiManager : UiManager {

    private val FragmentActivity.shouldShow
        get() = Finch.isUiEnabled && supportFragmentManager.findFragmentByTag(
            DebugMenuBottomSheet.TAG
        ) as? DebugMenuBottomSheet? == null
    private val FragmentActivity.dialogToHide
        get() = if (Finch.isUiEnabled) supportFragmentManager.findFragmentByTag(
            DebugMenuBottomSheet.TAG
        ) as? DebugMenuBottomSheet? else null

    override fun show(activity: FragmentActivity) = (activity.shouldShow).also { shouldShow ->
        if (shouldShow) {
            DebugMenuBottomSheet.show(activity.supportFragmentManager)
        }
    }

    override fun hide(activity: FragmentActivity?) = (activity?.dialogToHide)?.let {
        it.dismiss()
        true
    } ?: false
}
