package com.kernel.finch.common.contracts

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.listeners.*
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.common.models.Configuration
import com.kernel.finch.common.models.Position
import kotlin.reflect.KClass

interface Finch {

    //region core
    var isUiEnabled: Boolean
        get() = false
        set(_) = Unit

    fun initialize(
        application: Application,
        configuration: Configuration = Configuration(),
        vararg components: Component<*> = arrayOf()
    ) = Unit

    fun show(): Boolean = false

    fun hide(): Boolean = false
    //endregion

    //region components management
    fun set(vararg components: Component<*>) = Unit

    fun add(
        vararg components: Component<*>,
        position: Position = Position.Bottom,
        lifecycleOwner: LifecycleOwner? = null
    ) = Unit

    fun remove(vararg ids: String) = Unit

    fun contains(id: String): Boolean = false

    @Throws(ClassCastException::class)
    fun <M : Component<M>> find(id: String): M? = null

    fun <C : Component<C>> delegateFor(type: KClass<out C>): Component.Delegate<C>? = null
    //endregion

    //region listeners
    fun addLogListener(listener: LogListener, lifecycleOwner: LifecycleOwner? = null) = Unit

    fun removeLogListener(listener: LogListener) = Unit

    fun clearLogListeners() = Unit

    fun addNetworkLogListener(
        listener: NetworkLogListener,
        lifecycleOwner: LifecycleOwner? = null
    ) = Unit

    fun removeNetworkLogListener(listener: NetworkLogListener) = Unit

    fun clearNetworkLogListeners() = Unit

    fun addOverlayListener(listener: OverlayListener, lifecycleOwner: LifecycleOwner? = null) = Unit

    fun removeOverlayListener(listener: OverlayListener) = Unit

    fun clearOverlayListeners() = Unit

    fun addUpdateListener(listener: UpdateListener, lifecycleOwner: LifecycleOwner? = null) = Unit

    fun removeUpdateListener(listener: UpdateListener) = Unit

    fun clearUpdateListeners() = Unit

    fun addVisibilityListener(
        listener: VisibilityListener,
        lifecycleOwner: LifecycleOwner? = null
    ) = Unit

    fun removeVisibilityListener(listener: VisibilityListener) = Unit

    fun clearVisibilityListeners() = Unit
    //endregion

    //region helpers
    val currentActivity: FragmentActivity? get() = null

    fun log(message: CharSequence, label: String? = null, payload: CharSequence? = null) = Unit

    fun clearLogs(label: String? = null) = Unit

    fun logNetworkEvent(networkLog: NetworkLogEntity) = Unit

    fun clearNetworkLogs() = Unit

    fun takeScreenshot(callback: (Uri?) -> Unit) = callback.invoke(null)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun recordScreen(callback: (Uri?) -> Unit) = callback.invoke(null)

    fun openGallery() = Unit

    fun refresh() = Unit

    fun invalidateOverlay() = Unit

    fun showDialog(
        contents: CharSequence,
        timestamp: Long = System.currentTimeMillis(),
        isHorizontalScrollEnabled: Boolean = false
    ) = Unit

    fun showNetworkEventActivity(networkLog: NetworkLogEntity) = Unit

    fun showNetworkEventListActivity() = Unit

    companion object {
        const val FILE_NAME_DATE_TIME_FORMAT = "dd_MM_yyyy_HH_mm_ss"
        const val GALLERY_DATE_FORMAT = "dd.MM.yyyy"
        const val LOG_TIME_FORMAT = "HH:mm:ss"
        const val LOG_DATE_AND_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss"
    }
}
