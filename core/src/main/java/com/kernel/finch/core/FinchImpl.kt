package com.kernel.finch.core

import android.app.Application
import android.graphics.Canvas
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.listeners.*
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.common.models.Configuration
import com.kernel.finch.common.models.Position
import com.kernel.finch.components.special.LifecycleLogs
import com.kernel.finch.core.data.db.FinchDatabase
import com.kernel.finch.core.data.db.NetworkLogDao
import com.kernel.finch.core.data.models.Period
import com.kernel.finch.core.manager.*
import com.kernel.finch.core.manager.listener.*
import com.kernel.finch.core.presentation.detail.log.dialog.LogDetailDialogFragment
import com.kernel.finch.core.presentation.detail.networklog.NetworkLogActivity
import com.kernel.finch.core.presentation.gallery.MediaPreviewDialogFragment
import com.kernel.finch.core.util.FinchUtil
import com.kernel.finch.core.util.extension.hideKeyboard
import com.kernel.finch.utils.view.GestureBlockingRecyclerView
import kotlin.properties.Delegates
import kotlin.reflect.KClass

@Suppress("TooManyFunctions")
class FinchImpl(val uiManager: UiManager) : Finch {

    override var isUiEnabled by Delegates.observable(true) { _, _, newValue ->
        if (!newValue) {
            hide()
        }
    }
    override val currentActivity get() = debugMenuInjector.currentActivity
    var configuration = Configuration()
        private set
    lateinit var videoThumbnailLoader: ImageLoader
    internal val hasPendingUpdates get() = listManager.hasPendingUpdates
    internal val memoryStorageManager by lazy { MemoryStorageManager() }
    internal lateinit var localStorageManager: LocalStorageManager
        private set
    private lateinit var notificationManager: NotificationManager
    private lateinit var retentionManager: RetentionManager
    private lateinit var networkLogDao: NetworkLogDao
    private val debugMenuInjector by lazy { DebugMenuInjector(uiManager) }
    private val logListenerManager by lazy { LogListenerManager() }
    private val networkLogListenerManager by lazy { NetworkLogListenerManager() }
    private val overlayListenerManager by lazy { OverlayListenerManager() }
    private val updateListenerManager by lazy { UpdateListenerManager() }
    private val visibilityListenerManager by lazy { VisibilityListenerManager() }
    private val logManager by lazy { LogManager(logListenerManager, listManager, ::refresh) }
    private val lifecycleLogManager by lazy { LifecycleLogManager(listManager, ::refresh) }
    private val networkLogManager by lazy {
        NetworkLogManager(
            networkLogDao,
            retentionManager,
            networkLogListenerManager,
            listManager,
            ::refresh
        )
    }
    private val listManager by lazy { ListManager() }
    private val screenCaptureManager by lazy { ScreenCaptureManager() }
    internal var onScreenCaptureReady: ((Uri?) -> Unit)?
        get() = screenCaptureManager.onScreenCaptureReady
        set(value) {
            screenCaptureManager.onScreenCaptureReady = value
        }

    init {
        FinchCore.implementation = this
    }

    override fun initialize(
        application: Application,
        configuration: Configuration,
        vararg components: Component<*>
    ) {
        this.configuration =
            configuration.takeIf { it.themeResourceId != null } ?: configuration.copy(
                themeResourceId = R.style.Theme_Finch_Default
            )
        this.localStorageManager = LocalStorageManager(application)
        this.notificationManager = NotificationManager(application)
        this.networkLogDao = FinchDatabase.getInstance(application).networkLog()
        this.retentionManager = RetentionManager(application, Period.ONE_WEEK)
        debugMenuInjector.register(application)
        configuration.logger?.register(::log, ::clearLogs)
        configuration.networkLoggers.forEach { it.register(::logNetworkEvent, ::clearNetworkLogs) }
        videoThumbnailLoader = ImageLoader.Builder(application)
            .components(fun ComponentRegistry.Builder.() {
                add(VideoFrameDecoder.Factory())
            })
            .build()
        set(*components)
    }

    override fun show() = (currentActivity?.let { currentActivity ->
        if (currentActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) &&
            currentActivity.supportFragmentManager.findFragmentByTag(MediaPreviewDialogFragment.TAG) == null
        ) uiManager.show(currentActivity) else false
    } ?: false)

    override fun hide() = uiManager.hide(currentActivity)

    override fun set(vararg components: Component<*>) = listManager.setComponents(
        components.toList(),
        updateListenerManager::notifyListenersOnContentsChanged
    )

    override fun add(
        vararg components: Component<*>,
        position: Position,
        lifecycleOwner: LifecycleOwner?
    ) =
        listManager.addComponents(
            components.toList(),
            position,
            lifecycleOwner,
            updateListenerManager::notifyListenersOnContentsChanged
        )

    override fun remove(vararg ids: String) = listManager.removeComponents(
        ids.toList(),
        updateListenerManager::notifyListenersOnContentsChanged
    )

    override fun contains(id: String) = listManager.contains(id)

    override fun <M : Component<M>> find(id: String) = listManager.findComponent<M>(id)

    override fun <M : Component<M>> delegateFor(type: KClass<out M>) =
        listManager.findComponentDelegate(type)

    override fun addLogListener(listener: LogListener, lifecycleOwner: LifecycleOwner?) =
        logListenerManager.addListener(listener, lifecycleOwner)

    override fun removeLogListener(listener: LogListener) =
        logListenerManager.removeListener(listener)

    override fun clearLogListeners() = logListenerManager.clearListeners()

    override fun addNetworkLogListener(
        listener: NetworkLogListener,
        lifecycleOwner: LifecycleOwner?
    ) = networkLogListenerManager.addListener(listener, lifecycleOwner)

    override fun removeNetworkLogListener(listener: NetworkLogListener) =
        networkLogListenerManager.removeListener(listener)

    override fun clearNetworkLogListeners() = networkLogListenerManager.clearListeners()

    internal fun addInternalOverlayListener(listener: OverlayListener) =
        overlayListenerManager.addInternalListener(listener)

    override fun addOverlayListener(listener: OverlayListener, lifecycleOwner: LifecycleOwner?) =
        overlayListenerManager.addListener(listener, lifecycleOwner)

    override fun removeOverlayListener(listener: OverlayListener) =
        overlayListenerManager.removeListener(listener)

    fun addInternalUpdateListener(listener: UpdateListener) =
        updateListenerManager.addInternalListener(listener)

    override fun addUpdateListener(listener: UpdateListener, lifecycleOwner: LifecycleOwner?) =
        updateListenerManager.addListener(listener, lifecycleOwner)

    override fun removeUpdateListener(listener: UpdateListener) =
        updateListenerManager.removeListener(listener)

    override fun clearUpdateListeners() = updateListenerManager.clearListeners()

    override fun clearOverlayListeners() = overlayListenerManager.clearListeners()

    internal fun addInternalVisibilityListener(listener: VisibilityListener) =
        visibilityListenerManager.addInternalListener(listener)

    override fun addVisibilityListener(
        listener: VisibilityListener,
        lifecycleOwner: LifecycleOwner?
    ) = visibilityListenerManager.addListener(listener, lifecycleOwner)

    override fun removeVisibilityListener(listener: VisibilityListener) =
        visibilityListenerManager.removeListener(listener)

    override fun clearVisibilityListeners() = visibilityListenerManager.clearListeners()

    override fun log(message: CharSequence, label: String?, payload: CharSequence?) =
        logManager.log(label, message, payload)

    override fun clearLogs(label: String?) = logManager.clearLogs(label)

    override fun logNetworkEvent(networkLog: NetworkLogEntity) {
        networkLogManager.log(networkLog)
        if (configuration.showNotificationNetworkLoggers) notificationManager.show(networkLog)
    }

    override fun clearNetworkLogs() = networkLogManager.clearLogs()

    override fun takeScreenshot(callback: (Uri?) -> Unit) =
        screenCaptureManager.takeScreenshot(callback)

    override fun recordScreen(callback: (Uri?) -> Unit) =
        screenCaptureManager.recordScreen(callback)

    override fun openGallery() = screenCaptureManager.openGallery()

    override fun refresh() =
        listManager.refreshCells(updateListenerManager::notifyListenersOnContentsChanged)

    override fun invalidateOverlay() = debugMenuInjector.invalidateOverlay()

    override fun showDialog(
        contents: CharSequence,
        timestamp: Long,
        isHorizontalScrollEnabled: Boolean
    ) {
        (uiManager.findHostFragmentManager()
            ?: currentActivity?.supportFragmentManager)?.let { fragmentManager ->
            LogDetailDialogFragment.show(
                fragmentManager = fragmentManager,
                content = contents,
                timestamp = timestamp,
                isHorizontalScrollEnabled = isHorizontalScrollEnabled
            )
        }
    }

    override fun showNetworkEventActivity(networkLog: NetworkLogEntity) {
        currentActivity?.let {
            NetworkLogActivity.start(it, networkLog.id)
        }
    }

    override fun showNetworkEventListActivity() {
        currentActivity?.let {
            it.startActivity(FinchUtil.getLaunchIntent(it))
        }
    }

    internal fun applyPendingChanges() {
        listManager.applyPendingChanges()
        updateListenerManager.notifyListenersOnAllPendingChangesApplied()
    }

    internal fun resetPendingChanges() = listManager.resetPendingChanges()

    internal fun getLogEntries(tag: String?) = logManager.getEntries(tag)

    internal fun getLifecycleLogEntries(eventTypes: List<LifecycleLogs.EventType>) =
        lifecycleLogManager.getEntries(eventTypes)

    internal fun logLifecycle(
        classType: Class<*>,
        eventType: LifecycleLogs.EventType,
        hasSavedInstanceState: Boolean? = null
    ) = lifecycleLogManager.log(classType, eventType, hasSavedInstanceState)

    internal fun getNetworkLogEntries() = networkLogManager.getEntries()

    internal fun createOverlayLayout(activity: FragmentActivity) =
        uiManager.createOverlayLayout(activity)

    fun notifyVisibilityListenersOnShow() = visibilityListenerManager.notifyListenersOnShow()

    fun notifyVisibilityListenersOnHide() = visibilityListenerManager.notifyListenersOnHide()

    internal fun notifyOverlayListenersOnDrawOver(canvas: Canvas) =
        overlayListenerManager.notifyListeners(canvas)

    fun hideKeyboard() = currentActivity?.currentFocus?.hideKeyboard() ?: Unit

    internal fun setupRecyclerView(recyclerView: GestureBlockingRecyclerView) =
        listManager.setupRecyclerView(recyclerView)
}
