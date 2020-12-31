package com.kernel.finch.implementation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.kernel.finch.Finch
import com.kernel.finch.FinchCore
import com.kernel.finch.R
import com.kernel.finch.common.models.Inset
import com.kernel.finch.core.presentation.InternalDebugMenuView
import kotlin.math.min
import kotlin.math.roundToInt

@SuppressLint("ViewConstructor")
internal class DebugMenuDrawerLayout(
    context: Context,
    overlayFrameLayout: View,
    val debugMenuView: InternalDebugMenuView
) : DrawerLayout(context) {

    private val listener = object : DrawerListener {

        override fun onDrawerOpened(drawerView: View) = Unit

        override fun onDrawerClosed(drawerView: View) =
            FinchCore.implementation.notifyVisibilityListenersOnHide()

        override fun onDrawerStateChanged(newState: Int) {
            if (newState == STATE_DRAGGING && !isDrawerOpen(debugMenuView)) {
                FinchCore.implementation.notifyVisibilityListenersOnShow()
            }
        }

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit
    }

    init {
        addView(
            overlayFrameLayout,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        )
        addView(
            debugMenuView,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, GravityCompat.END)
        )
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?) = isDrawerOpen(debugMenuView) && try {
        super.onInterceptTouchEvent(ev)
    } catch (_: NullPointerException) {
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent) =
        if (isDrawerVisible(debugMenuView) || ev.action != MotionEvent.ACTION_DOWN) {
            super.onTouchEvent(ev)
        } else (((width - 2 * ViewConfiguration.get(context).scaledTouchSlop) <= ev.x) && super.onTouchEvent(
            ev
        ))

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setDrawerLockMode(if (Finch.isUiEnabled) LOCK_MODE_UNDEFINED else LOCK_MODE_LOCKED_CLOSED)
        addDrawerListener(listener)
        setDrawerSize(DisplayMetrics())
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeDrawerListener(listener)
    }

    private fun setDrawerSize(displayMetrics: DisplayMetrics) {
        if (displayMetrics.widthPixels == 0) {
            post {
                FinchCore.implementation.currentActivity?.windowManager?.defaultDisplay?.getMetrics(
                    displayMetrics
                )
                setDrawerSize(displayMetrics)
            }
        } else {
            debugMenuView.run {
                layoutParams = layoutParams.apply {
                    width = min(
                        resources.getDimensionPixelSize(R.dimen.finch_drawer_maximum_width),
                        (displayMetrics.widthPixels * DRAWER_WIDTH_RATIO).roundToInt()
                    )
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                Finch.currentActivity?.window?.decorView?.run {
                    setOnApplyWindowInsetsListener { _, insets ->
                        onApplyWindowInsets(insets).also {
                            val input = Inset(
                                left = it.systemWindowInsetLeft,
                                top = it.systemWindowInsetTop,
                                right = it.systemWindowInsetRight,
                                bottom = it.systemWindowInsetBottom
                            )
                            val output =
                                FinchCore.implementation.configuration.applyInsets?.invoke(input)
                                    ?: Inset(
                                        left = 0,
                                        top = it.systemWindowInsetTop,
                                        right = it.systemWindowInsetRight,
                                        bottom = it.systemWindowInsetBottom
                                    )
                            debugMenuView.applyInsets(
                                output.left,
                                output.top,
                                output.right,
                                output.bottom
                            )
                        }
                    }
                    requestApplyInsets()
                }
            }
        }
    }

    companion object {
        private const val DRAWER_WIDTH_RATIO = 0.6f
    }
}
