package com.kernel.finch.core.presentation.detail.log.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.applyTheme
import com.kernel.finch.core.util.extension.text
import com.kernel.finch.core.util.extension.viewModel
import com.kernel.finch.utils.consume
import com.kernel.finch.utils.extensions.argument
import com.kernel.finch.utils.extensions.colorResource
import com.kernel.finch.utils.extensions.tintedDrawable

internal class LogDetailDialogFragment : DialogFragment() {

    private lateinit var appBar: AppBarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var textView: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var shareButton: MenuItem
    private val scrollListener =
        ViewTreeObserver.OnScrollChangedListener { appBar.setLifted(scrollView.scrollY != 0) }
    private val viewModel by viewModel<LogDetailDialogViewModel>()

    private var content: CharSequence by argument()
    private var timestamp: Long by argument()
    private var isHorizontalScrollEnabled: Boolean by argument()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext().applyTheme())
            .setView(if (isHorizontalScrollEnabled) R.layout.finch_dialog_fragment_log_detail_scrolling else R.layout.finch_dialog_fragment_log_detail)
            .create()

    override fun onResume() {
        super.onResume()
        dialog?.let { dialog ->
            appBar = dialog.findViewById(R.id.finch_app_bar)
            toolbar = dialog.findViewById(R.id.finch_toolbar)
            textView = dialog.findViewById(R.id.finch_text_view)
            scrollView = dialog.findViewById(R.id.finch_scroll_view)
            textView.text = content
            appBar.run {
                setPadding(0, 0, 0, 0)
                setBackgroundColor(context.colorResource(R.attr.colorBackgroundFloating))
            }
            scrollView.viewTreeObserver.addOnScrollChangedListener(scrollListener)
            toolbar.run {
                val textColor = context.colorResource(android.R.attr.textColorPrimary)
                setNavigationOnClickListener { dismiss() }
                navigationIcon = context.tintedDrawable(R.drawable.finch_ic_close, textColor)
                shareButton = menu.findItem(R.id.finch_share).also {
                    it.title = context.text(R.string.finch_share)
                    it.icon = context.tintedDrawable(R.drawable.finch_ic_share, textColor)
                }
                setOnMenuItemClickListener(::onMenuItemClicked)
            }
            viewModel.isShareButtonEnabled.observe(this, Observer { shareButton.isEnabled = it })
        }
    }

    override fun onPause() {
        super.onPause()
        scrollView.viewTreeObserver.removeOnScrollChangedListener(scrollListener)
    }

    private fun onMenuItemClicked(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.finch_share -> consume {
            viewModel.shareLogs(
                activity,
                textView.text,
                timestamp
            )
        }
        else -> false
    }

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            content: CharSequence,
            timestamp: Long,
            isHorizontalScrollEnabled: Boolean
        ) =
            LogDetailDialogFragment()
                .apply {
                    this.content = content
                    this.timestamp = timestamp
                    this.isHorizontalScrollEnabled = isHorizontalScrollEnabled
                }.run {
                    show(fragmentManager, tag)
                }
    }
}
