package com.kernel.finch.core.presentation.gallery

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kernel.finch.FinchCore
import com.kernel.finch.core.R
import com.kernel.finch.core.manager.ScreenCaptureManager
import com.kernel.finch.core.presentation.gallery.list.GalleryAdapter
import com.kernel.finch.core.util.extension.*
import com.kernel.finch.utils.consume
import com.kernel.finch.utils.extensions.colorResource
import com.kernel.finch.utils.extensions.dimension
import com.kernel.finch.utils.extensions.tintedDrawable

internal class GalleryActivity : AppCompatActivity(),
    DeleteConfirmationDialogFragment.OnPositiveButtonClickedListener {

    private val viewModel by viewModel<GalleryViewModel>()
    private val contentPadding by lazy { dimension(R.dimen.finch_content_padding_medium) }
    private lateinit var shareButton: MenuItem
    private lateinit var deleteButton: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        FinchCore.implementation.configuration.themeResourceId?.let { setTheme(it) }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finch_activity_gallery)
        supportActionBar?.hide()
        findViewById<Toolbar>(R.id.finch_toolbar).apply {
            val textColor = colorResource(android.R.attr.textColorPrimary)
            setNavigationOnClickListener { supportFinishAfterTransition() }
            navigationIcon = tintedDrawable(R.drawable.finch_ic_close, textColor)
            title = text(R.string.finch_gallery)
            shareButton = menu.findItem(R.id.finch_share).also {
                it.title = text(R.string.finch_share)
                it.icon = tintedDrawable(R.drawable.finch_ic_share, textColor)
            }
            deleteButton = menu.findItem(R.id.finch_delete).also {
                it.title = text(R.string.finch_delete)
                it.icon = tintedDrawable(R.drawable.finch_ic_delete, textColor)
            }
            setOnMenuItemClickListener(::onMenuItemClicked)
        }
        viewModel.isInSelectionMode.observe(this, Observer {
            shareButton.isVisible = it
            deleteButton.isVisible = it
        })
        val emptyStateTextView = findViewById<TextView>(R.id.finch_text_view)
        emptyStateTextView.text = text(R.string.finch_no_media_message)
        val largePadding = dimension(R.dimen.finch_content_padding_extra_large)
        val recyclerView = findViewById<RecyclerView>(R.id.finch_recycler_view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val bottomNavigationOverlay = findViewById<View>(R.id.finch_bottom_navigation_overlay)
            bottomNavigationOverlay.setBackgroundColor(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.navigationBarColor else Color.BLACK)
            window.decorView.run {
                setOnApplyWindowInsetsListener { _, insets ->
                    onApplyWindowInsets(insets).also {
                        recyclerView.setPadding(
                            it.systemWindowInsetLeft + contentPadding,
                            contentPadding,
                            it.systemWindowInsetRight + contentPadding,
                            it.systemWindowInsetBottom + contentPadding
                        )
                        bottomNavigationOverlay.run {
                            layoutParams =
                                layoutParams.apply { height = it.systemWindowInsetBottom }
                        }
                        emptyStateTextView.setPadding(
                            largePadding,
                            largePadding,
                            largePadding,
                            largePadding + it.systemWindowInsetBottom
                        )
                    }
                }
                requestApplyInsets()
            }
        }
        val adapter = GalleryAdapter(
            onMediaSelected = { position -> viewModel.items.value?.get(position)?.id?.let(::onItemSelected) },
            onLongTap = { position -> viewModel.items.value?.get(position)?.id?.let(viewModel::selectItem) }
        )
        recyclerView.setHasFixedSize(true)
        val spanCount = getSpanCount()
        recyclerView.layoutManager = GridLayoutManager(this, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) =
                    if (viewModel.isSectionHeader(position)) spanCount else 1
            }
        }
        recyclerView.adapter = adapter
        viewModel.items.observe(this, {
            adapter.submitList(it)
            emptyStateTextView.visible = it.isEmpty()
        })
    }

    override fun onResume() {
        super.onResume()
        loadMedia()
    }

    override fun onPositiveButtonClicked() = viewModel.deleteSelectedItems()

    fun loadMedia() = viewModel.loadMedia(this)

    private fun onMenuItemClicked(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.finch_share -> consume { shareItems(viewModel.selectedItemIds) }
        R.id.finch_delete -> consume {
            DeleteConfirmationDialogFragment.show(
                supportFragmentManager
            )
        }
        else -> false
    }

    private fun onItemSelected(fileName: String) {
        if (viewModel.isInSelectionMode.value == true) {
            viewModel.selectItem(fileName)
        } else {
            MediaPreviewDialogFragment.show(supportFragmentManager, fileName)
        }
    }

    private fun shareItem(fileName: String) {
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

    private fun shareItems(fileNames: List<String>) {
        if (fileNames.size == 1) {
            shareItem(fileNames.first())
        } else {
            shareFiles(fileNames.map { fileName ->
                getUriForFile(
                    getScreenCapturesFolder().resolve(
                        fileName
                    )
                )
            })
        }
    }

    override fun onBackPressed() {
        if (viewModel.isInSelectionMode.value == true) {
            viewModel.exitSelectionMode()
        } else {
            super.onBackPressed()
        }
    }

    private fun getSpanCount(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.widthPixels / dimension(R.dimen.finch_gallery_item_min_size)
    }
}
