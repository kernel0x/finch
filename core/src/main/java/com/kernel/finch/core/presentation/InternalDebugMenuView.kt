package com.kernel.finch.core.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.kernel.finch.FinchCore
import com.kernel.finch.common.listeners.UpdateListener
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.applyTheme
import com.kernel.finch.utils.extensions.colorResource
import com.kernel.finch.utils.extensions.dimension
import com.kernel.finch.utils.extensions.drawable
import com.kernel.finch.utils.view.GestureBlockingRecyclerView

class InternalDebugMenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context.applyTheme(), attrs, defStyleAttr),
    UpdateListener {

    private val verticalMargin = context.dimension(R.dimen.finch_item_vertical_margin)
    private var recyclerLeftPadding = 0
    private var recyclerTopPadding = verticalMargin
    private var recyclerRightPadding = 0
    private var recyclerBottomPadding = verticalMargin

    //TODO: Create a custom view for the button container and all related logic and improve their appearance
    private val largePadding = context.dimension(R.dimen.finch_content_padding_extra_large)
    private val applyButton =
        AppCompatButton(context.applyTheme(), attrs, androidx.appcompat.R.attr.buttonStyle).apply {
            isAllCaps = false
            setText(R.string.finch_apply)
            setPadding(largePadding, largePadding, largePadding, largePadding)
            setOnClickListener { FinchCore.implementation.applyPendingChanges() }
        }
    private val resetButton =
        AppCompatButton(context.applyTheme(), attrs, androidx.appcompat.R.attr.buttonStyle).apply {
            isAllCaps = false
            setText(R.string.finch_reset)
            setPadding(largePadding, largePadding, largePadding, largePadding)
            setOnClickListener { FinchCore.implementation.resetPendingChanges() }
        }
    private val buttonContainer = LinearLayout(context.applyTheme(), attrs, defStyleAttr).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        addView(
            applyButton,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                leftMargin = largePadding
                rightMargin = largePadding / 2
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    marginStart = leftMargin
                    marginEnd = rightMargin
                }
            })
        clipChildren = false
        visibility = View.INVISIBLE
        addView(
            resetButton,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                leftMargin = largePadding / 2
                rightMargin = largePadding
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    marginStart = leftMargin
                    marginEnd = rightMargin
                }
            })
        background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.TRANSPARENT,
                context.applyTheme().colorResource(android.R.attr.textColorPrimary)
            )
        )
    }
    private val recyclerView =
        GestureBlockingRecyclerView(context.applyTheme(), attrs, defStyleAttr).apply {
            clipToPadding = false
            minimumWidth = context.dimension(R.dimen.finch_debug_menu_view_min_size)
            minimumHeight = context.dimension(R.dimen.finch_debug_menu_view_min_size)
        }

    init {
        setBackground()
        addView(recyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(
            buttonContainer,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM
            })
        onContentsChanged()
        applyInsets(0, 0, 0, 0)
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        FinchCore.implementation.addInternalUpdateListener(this)
        FinchCore.implementation.setupRecyclerView(recyclerView)
    }

    @CallSuper
    override fun onDetachedFromWindow() {
        FinchCore.implementation.removeUpdateListener(this)
        recyclerView.adapter = null
        super.onDetachedFromWindow()
    }

    override fun onContentsChanged() {
        val hasPendingChanges = try {
            FinchCore.implementation.hasPendingUpdates
        } catch (_: ConcurrentModificationException) {
            false
        }
        recyclerView.updatePadding(hasPendingChanges)
        buttonContainer.run {
            if (hasPendingChanges) {
                visibility = View.VISIBLE
            }
            post {
                animate()
                    .alpha(if (hasPendingChanges) 1f else 0f)
                    .start()
                applyButton.animate().translationY(if (hasPendingChanges) 0f else height.toFloat())
                    .start()
                resetButton.animate().translationY(if (hasPendingChanges) 0f else height.toFloat())
                    .start()
            }
        }
    }

    fun applyInsets(left: Int, top: Int, right: Int, bottom: Int) {
        val scrollBy = recyclerTopPadding - top - verticalMargin
        recyclerLeftPadding = left
        recyclerTopPadding = top + verticalMargin
        recyclerRightPadding = right
        recyclerBottomPadding = bottom + verticalMargin
        buttonContainer.setPadding(left, top, right, bottom + largePadding)
        recyclerView.updatePadding(FinchCore.implementation.hasPendingUpdates)
        recyclerView.scrollBy(0, scrollBy)
    }

    private fun setBackground() {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            setBackgroundColor(typedValue.data)
        } else {
            background = context.drawable(typedValue.resourceId)
        }
    }

    private fun RecyclerView.updatePadding(hasPendingUpdates: Boolean) = setPadding(
        recyclerLeftPadding,
        recyclerTopPadding,
        recyclerRightPadding,
        if (hasPendingUpdates) verticalMargin + buttonContainer.height else recyclerBottomPadding
    )
}
