package com.kernel.finch.core

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.kernel.finch.FinchCore
import com.kernel.finch.core.util.ScreenCaptureService

internal class OverlayFragment : Fragment() {

    private var fileName = "file"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FinchCore.implementation.createOverlayLayout(requireActivity())

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startCapture(isForVideo: Boolean, fileName: String) {
        (context?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager?).let { mediaProjectionManager ->
            if (mediaProjectionManager == null) {
                FinchCore.implementation.onScreenCaptureReady?.invoke(null)
            } else {
                this.fileName = fileName
                startActivityForResult(
                    mediaProjectionManager.createScreenCaptureIntent(),
                    if (isForVideo) SCREEN_RECORDING_REQUEST else SCREENSHOT_REQUEST
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SCREENSHOT_REQUEST,
            SCREEN_RECORDING_REQUEST -> {
                if (data == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    FinchCore.implementation.onScreenCaptureReady?.invoke(null)
                } else {
                    requireContext().run {
                        startService(
                            ScreenCaptureService.getStartIntent(
                                this,
                                resultCode,
                                data,
                                requestCode == SCREEN_RECORDING_REQUEST,
                                fileName
                            )
                        )
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val TAG = "finchOverlayFragment"

        private const val SCREENSHOT_REQUEST = 4246
        private const val SCREEN_RECORDING_REQUEST = 4247

        fun newInstance() = OverlayFragment()
    }
}