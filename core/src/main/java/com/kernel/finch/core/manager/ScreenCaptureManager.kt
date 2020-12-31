package com.kernel.finch.core.manager

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.kernel.finch.FinchCore
import com.kernel.finch.core.presentation.gallery.GalleryActivity
import com.kernel.finch.core.util.extension.recordScreenWithMediaProjectionManager
import com.kernel.finch.core.util.extension.takeScreenshotWithDrawingCache
import com.kernel.finch.core.util.extension.takeScreenshotWithMediaProjectionManager
import com.kernel.finch.core.util.performOnHide

internal class ScreenCaptureManager {

    var onScreenCaptureReady: ((Uri?) -> Unit)? = null
    private val currentActivity get() = FinchCore.implementation.currentActivity
    private val behavior get() = FinchCore.implementation.configuration

    fun takeScreenshot(callback: (Uri?) -> Unit) {
        if (onScreenCaptureReady != null) {
            callback(null)
        } else {
            onScreenCaptureReady = { uri ->
                callback(uri)
                onScreenCaptureReady = null
            }
            currentActivity?.run {
                val fileName =
                    "${behavior.imageFileNameFormatter(System.currentTimeMillis())}$IMAGE_EXTENSION"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    takeScreenshotWithMediaProjectionManager(fileName)
                } else {
                    takeScreenshotWithDrawingCache(fileName)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun recordScreen(callback: (Uri?) -> Unit) {
        if (onScreenCaptureReady != null) {
            callback(null)
        } else {
            onScreenCaptureReady = { uri ->
                callback(uri)
                onScreenCaptureReady = null
            }
            currentActivity?.recordScreenWithMediaProjectionManager(
                "${
                    behavior.videoFileNameFormatter(
                        System.currentTimeMillis()
                    )
                }$VIDEO_EXTENSION"
            )
        }
    }

    fun openGallery() = performOnHide {
        currentActivity?.run {
            startActivity(
                Intent(
                    this,
                    GalleryActivity::class.java
                )
            )
        }
    }

    companion object {
        const val IMAGE_EXTENSION = ".png"
        const val IMAGE_TYPE = "image/png"
        const val VIDEO_EXTENSION = ".mp4"
        const val VIDEO_TYPE = "video/mp4"
    }
}
