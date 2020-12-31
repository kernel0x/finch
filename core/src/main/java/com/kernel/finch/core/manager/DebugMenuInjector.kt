package com.kernel.finch.core.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.kernel.finch.FinchCore
import com.kernel.finch.components.special.LifecycleLogs
import com.kernel.finch.core.OverlayFragment
import com.kernel.finch.core.presentation.detail.log.dialog.LogDetailDialogFragment
import com.kernel.finch.core.presentation.gallery.MediaPreviewDialogFragment
import com.kernel.finch.core.util.SimpleActivityLifecycleCallbacks
import com.kernel.finch.core.util.extension.supportsDebugMenu

@Suppress("TooManyFunctions")
internal class DebugMenuInjector(
    private val uiManager: UiManager
) {
    var currentActivity: FragmentActivity? = null
        private set(value) {
            field = value
            if (value != null) {
                onBackStackChangedListener.onBackStackChanged()
            }
        }
    private val onBackStackChangedListener = FragmentManager.OnBackStackChangedListener {
        currentActivity?.let { currentActivity ->
            if (currentActivity.supportFragmentManager.fragments.lastOrNull() !is OverlayFragment) {
                uiManager.addOverlayFragment(currentActivity)
            }
        }
    }
    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.FRAGMENT_ON_ATTACH
                )
            }
        }

        override fun onFragmentActivityCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.FRAGMENT_ON_ACTIVITY_CREATED,
                    savedInstanceState != null
                )
            }
        }

        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.ON_CREATE,
                    savedInstanceState != null
                )
            }
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.FRAGMENT_ON_VIEW_CREATED,
                    savedInstanceState != null
                )
            }
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.ON_START
                )
            }
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.ON_RESUME
                )
            }
        }

        override fun onFragmentSaveInstanceState(
            fm: FragmentManager,
            f: Fragment,
            outState: Bundle
        ) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.ON_SAVE_INSTANCE_STATE
                )
            }
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.ON_PAUSE
                )
            }
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.ON_STOP
                )
            }
        }

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.FRAGMENT_ON_VIEW_DESTROYED
                )
            }
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.ON_DESTROY
                )
            }
        }

        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            if (f.shouldLogFragment()) {
                FinchCore.implementation.logLifecycle(
                    f::class.java,
                    LifecycleLogs.EventType.FRAGMENT_ON_DETACH
                )
            }
        }
    }
    private val activityLifecycleCallbacks = object : SimpleActivityLifecycleCallbacks() {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity.supportsDebugMenu) {
                (activity as FragmentActivity).supportFragmentManager.run {
                    removeOnBackStackChangedListener(onBackStackChangedListener)
                    addOnBackStackChangedListener(onBackStackChangedListener)
                    unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
                    registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
                }
                FinchCore.implementation.logLifecycle(
                    activity::class.java,
                    LifecycleLogs.EventType.ON_CREATE,
                    savedInstanceState != null
                )
            }
        }

        override fun onActivityStarted(activity: Activity) {
            if (activity.supportsDebugMenu) {
                FinchCore.implementation.logLifecycle(
                    activity::class.java,
                    LifecycleLogs.EventType.ON_START
                )
            }
        }

        override fun onActivityResumed(activity: Activity) {
            if (currentActivity != activity) {
                currentActivity =
                    if (activity.supportsDebugMenu) activity as FragmentActivity else null
                FinchCore.implementation.refresh()
            }
            if (activity.supportsDebugMenu) {
                FinchCore.implementation.logLifecycle(
                    activity::class.java,
                    LifecycleLogs.EventType.ON_RESUME
                )
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
            if (activity.supportsDebugMenu) {
                FinchCore.implementation.logLifecycle(
                    activity::class.java,
                    LifecycleLogs.EventType.ON_SAVE_INSTANCE_STATE
                )
            }
        }

        override fun onActivityPaused(activity: Activity) {
            if (activity.supportsDebugMenu) {
                FinchCore.implementation.logLifecycle(
                    activity::class.java,
                    LifecycleLogs.EventType.ON_PAUSE
                )
            }
        }

        override fun onActivityStopped(activity: Activity) {
            if (activity.supportsDebugMenu) {
                FinchCore.implementation.logLifecycle(
                    activity::class.java,
                    LifecycleLogs.EventType.ON_STOP
                )
            }
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (activity.supportsDebugMenu) {
                if (activity == currentActivity) {
                    currentActivity = null
                }
                (activity as FragmentActivity).supportFragmentManager.removeOnBackStackChangedListener(
                    onBackStackChangedListener
                )
                FinchCore.implementation.logLifecycle(
                    activity::class.java,
                    LifecycleLogs.EventType.ON_DESTROY
                )
            }
        }
    }

    internal fun invalidateOverlay() {
        uiManager.findOverlayView(currentActivity)?.postInvalidate()
    }

    internal fun register(application: Application) = application.run {
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun Fragment.shouldLogFragment() =
        this !is OverlayFragment && this !is MediaPreviewDialogFragment && this !is LogDetailDialogFragment
}
