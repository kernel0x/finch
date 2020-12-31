package com.kernel.finch.core.presentation.gallery

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import coil.load
import coil.request.ImageRequest
import com.kernel.finch.FinchCore
import com.kernel.finch.core.R
import com.kernel.finch.core.manager.ScreenCaptureManager
import com.kernel.finch.core.util.extension.*
import com.kernel.finch.utils.consume
import com.kernel.finch.utils.extensions.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.max

class MediaPreviewDialogFragment : DialogFragment(),
    DeleteConfirmationDialogFragment.OnPositiveButtonClickedListener {

    private var fileName: String by argument()
    private val toolbar get() = dialog?.findViewById<Toolbar>(R.id.finch_toolbar)
    private val imageView get() = dialog?.findViewById<ImageView>(R.id.finch_image_view)
    private val videoView get() = dialog?.findViewById<VideoView>(R.id.finch_video_view)
    private lateinit var shareButton: MenuItem
    private lateinit var deleteButton: MenuItem
    private var isLoaded = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext().applyTheme())
            .setView(R.layout.finch_dialog_fragment_media_preview)
            .create()

    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            context?.let { context ->
                imageView?.run {
                    if (fileName.endsWith(ScreenCaptureManager.IMAGE_EXTENSION)) {
                        load(context.getScreenCapturesFolder().resolve(fileName)) {
                            listener { _, _ -> setDialogSizeFromImage(this@run) }
                        }
                    } else {
                        GlobalScope.launch {
                            FinchCore.implementation.videoThumbnailLoader.execute(
                                ImageRequest.Builder(context)
                                    .data(context.getScreenCapturesFolder().resolve(fileName))
                                    .target(this@run)
                                    .listener { _, _ -> setDialogSizeFromImage(this@run) }
                                    .build()
                            )
                        }
                    }
                }
                toolbar?.run {
                    val textColor = context.colorResource(android.R.attr.textColorPrimary)
                    setNavigationOnClickListener { dismiss() }
                    navigationIcon = context.tintedDrawable(R.drawable.finch_ic_close, textColor)
                    shareButton = menu.findItem(R.id.finch_share).also {
                        it.title = context.text(R.string.finch_share)
                        it.icon = context.tintedDrawable(R.drawable.finch_ic_share, textColor)
                    }
                    deleteButton = menu.findItem(R.id.finch_delete).also {
                        it.title = context.text(R.string.finch_delete)
                        it.icon = context.tintedDrawable(R.drawable.finch_ic_delete, textColor)
                    }
                    setOnMenuItemClickListener(::onMenuItemClicked)
                }
            }
        }
    }

    override fun onPositiveButtonClicked() {
        GlobalScope.launch {
            activity?.run {
                getScreenCapturesFolder().resolve(fileName).delete()
                (this as? GalleryActivity)?.loadMedia()
            }
        }
        dismiss()
    }

    private fun setDialogSizeFromImage(imageView: ImageView) {
        imageView.run {
            waitForPreDraw {
                dialog?.window?.let { window ->
                    val padding = context.dimension(R.dimen.finch_content_padding_medium)
                    if (window.decorView.width > width + padding * 8) {
                        window.setLayout(
                            max(
                                width + padding * 4,
                                context.dimension(R.dimen.finch_gallery_preview_min_width)
                            ),
                            height + (toolbar?.height ?: 0) + padding
                        )
                    }
                }
                waitForPreDraw {
                    visible = true
                    isLoaded = true
                    if (fileName.endsWith(ScreenCaptureManager.VIDEO_EXTENSION)) {
                        videoView?.run {
                            visible = true
                            setOnPreparedListener { it.isLooping = true }
                            setVideoPath(context.getScreenCapturesFolder().resolve(fileName).path)
                            start()
                        }
                    }
                }
            }
        }
    }

    private fun onMenuItemClicked(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.finch_share -> consume(::shareItem)
        R.id.finch_delete -> consume {
            DeleteConfirmationDialogFragment.show(
                childFragmentManager
            )
        }
        else -> false
    }

    private fun shareItem() {
        activity?.run {
            val uri = getUriForFile(getScreenCapturesFolder().resolve(fileName))
            when {
                fileName.endsWith(ScreenCaptureManager.IMAGE_EXTENSION) -> shareFile(
                    uri,
                    ScreenCaptureManager.IMAGE_TYPE
                )
                fileName.endsWith(ScreenCaptureManager.VIDEO_EXTENSION) -> shareFile(
                    uri,
                    ScreenCaptureManager.VIDEO_TYPE
                )
            }
        }
    }

    companion object {

        const val TAG = "finchMediaPreviewDialogFragment"

        fun show(fragmentManager: FragmentManager, fileName: String) {
            if (fragmentManager.findFragmentByTag(TAG) == null) {
                MediaPreviewDialogFragment()
                    .apply {
                        this.fileName = fileName
                    }.run {
                        show(fragmentManager, TAG)
                    }
            }
        }
    }
}
