package com.kernel.finch.implementation

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import com.kernel.finch.FinchCore
import com.kernel.finch.common.models.Inset
import com.kernel.finch.core.presentation.InternalDebugMenuView
import com.kernel.finch.core.util.extension.applyTheme
import kotlin.math.roundToInt

internal class DebugMenuDialog : AppCompatDialogFragment() {

    override fun getContext() = super.getContext()?.applyTheme()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = InternalDebugMenuView(requireContext()).also {
        if (savedInstanceState == null) {
            FinchCore.implementation.notifyVisibilityListenersOnShow()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.run {
            val displayMetrics = DisplayMetrics()
            FinchCore.implementation.currentActivity?.run {
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val output = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.decorView.rootWindowInsets?.let {
                        FinchCore.implementation.configuration.applyInsets?.invoke(
                            Inset(
                                left = it.systemWindowInsetLeft,
                                top = it.systemWindowInsetTop,
                                right = it.systemWindowInsetRight,
                                bottom = it.systemWindowInsetBottom
                            )
                        )
                    }
                } else null
                val verticalInsets = output?.let { it.top + it.bottom }
                    ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) window.decorView.rootWindowInsets?.let { it.systemWindowInsetTop + it.systemWindowInsetBottom }
                        ?: 0 else 0
                val horizontalInsets = output?.let { it.left + it.right }
                    ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) window.decorView.rootWindowInsets?.let { it.systemWindowInsetLeft + it.systemWindowInsetRight }
                        ?: 0 else 0
                setLayout(
                    ((displayMetrics.widthPixels - horizontalInsets) * DIALOG_WIDTH_RATIO).roundToInt(),
                    ((displayMetrics.heightPixels - verticalInsets) * DIALOG_HEIGHT_RATIO).roundToInt()
                )
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isStateSaved) {
            FinchCore.implementation.notifyVisibilityListenersOnHide()
        }
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO = 0.75f
        private const val DIALOG_HEIGHT_RATIO = 0.75f
        const val TAG = "debugMenuDialog"

        fun show(fragmentManager: FragmentManager) = DebugMenuDialog().show(fragmentManager, TAG)
    }
}
