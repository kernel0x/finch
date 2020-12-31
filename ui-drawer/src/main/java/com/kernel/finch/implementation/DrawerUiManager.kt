package com.kernel.finch.implementation

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.kernel.finch.Finch
import com.kernel.finch.FinchCore
import com.kernel.finch.core.manager.UiManager
import com.kernel.finch.core.presentation.InternalDebugMenuView

internal class DrawerUiManager : UiManager, DrawerLayout.DrawerListener {

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            Finch.currentActivity?.let { hide(it) }
        }
    }

    override fun createOverlayLayout(activity: FragmentActivity) =
        InternalDebugMenuView(activity).let { drawer ->
            activity.onBackPressedDispatcher.addCallback(activity, onBackPressedCallback)
            DebugMenuDrawerLayout(
                context = activity,
                overlayFrameLayout = super.createOverlayLayout(activity),
                debugMenuView = drawer
            ).apply {
                updateDrawerLockMode()
                if (onBackPressedCallback.isEnabled) {
                    openDrawer(drawer, false)
                }
                activity.lifecycle.addObserver(object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_START)
                    fun onStart() = addDrawerListener(this@DrawerUiManager)

                    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                    fun onStop() = removeDrawerListener(this@DrawerUiManager)
                })
            }
        }

    override fun findOverlayView(activity: FragmentActivity?): View? =
        getDrawerLayout(activity)?.getChildAt(0)

    override fun show(activity: FragmentActivity) = if (Finch.isUiEnabled) {
        getDrawerView(activity)?.let { drawer ->
            FinchCore.implementation.notifyVisibilityListenersOnShow()
            (drawer.parent as DebugMenuDrawerLayout).let { drawerLayout ->
                drawerLayout.isDrawerVisible(drawer).let { isDrawerOpen ->
                    drawerLayout.openDrawer(drawer)
                    !isDrawerOpen
                }
            }
        } ?: false
    } else false

    override fun hide(activity: FragmentActivity?): Boolean {
        val drawer = getDrawerView(activity)
        val drawerLayout = drawer?.parent as? DebugMenuDrawerLayout?
        return (drawerLayout?.isDrawerOpen(drawer) == true).also {
            drawerLayout?.closeDrawers()
        }
    }

    override fun onDrawerStateChanged(newState: Int) = Unit

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) =
        FinchCore.implementation.hideKeyboard()

    override fun onDrawerClosed(drawerView: View) {
        onBackPressedCallback.isEnabled = false
    }

    override fun onDrawerOpened(drawerView: View) {
        onBackPressedCallback.isEnabled = true
    }

    private fun getDrawerView(activity: FragmentActivity?) =
        getDrawerLayout(activity)?.debugMenuView

    private fun getDrawerLayout(activity: FragmentActivity?) =
        findOverlayFragment(activity)?.view as? DebugMenuDrawerLayout?

    private fun DebugMenuDrawerLayout.updateDrawerLockMode() =
        setDrawerLockMode(if (Finch.isUiEnabled) DrawerLayout.LOCK_MODE_UNDEFINED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
}
