package com.kernel.finch.core.manager

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.FinchListItem
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.contracts.component.ValueWrapperComponent
import com.kernel.finch.common.models.Position
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.*
import com.kernel.finch.components.special.*
import com.kernel.finch.core.list.CellAdapter
import com.kernel.finch.core.list.delegates.*
import com.kernel.finch.utils.view.GestureBlockingRecyclerView
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.reflect.KClass

internal class ListManager {

    val hasPendingUpdates get() = persistableComponents.any { it.hasPendingChanges(FinchCore.implementation) }
    private var countDownJob: Job? = null
    private var shouldBlockGestures = true
    private val cellAdapter = CellAdapter()
    private val componentManagerContext = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    private val components = mutableListOf<Component<*>>(
        Label(
            id = HINT_COMPONENT_ID,
            text = Text.CharSequence(
                SpannableString("Welcome to Finch!\n\nUse Finch.set() or Finch.add() to add components to the debug menu.").apply {
                    setSpan(StyleSpan(Typeface.BOLD), 0, 18, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    setSpan(StyleSpan(Typeface.ITALIC), 24, 36, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    setSpan(StyleSpan(Typeface.ITALIC), 40, 52, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            )
        )
    )
    private val componentDelegates = mutableMapOf(
        AnimationSpeed::class to AnimationSpeedDelegate(),
        AppInfo::class to AppInfoDelegate(),
        CheckBox::class to CheckBoxDelegate(),
        DeveloperOptions::class to DeveloperOptionsDelegate(),
        DeviceInfo::class to DeviceInfoDelegate(),
        Divider::class to DividerDelegate(),
        ForceCrash::class to ForceCrashDelegate(),
        Gallery::class to GalleryDelegate(),
        Header::class to HeaderDelegate(),
        LifecycleLogs::class to LifecycleLogsDelegate(),
        ProgressBar::class to ProgressBarDelegate(),
        Logs::class to LogsDelegate(),
        LongText::class to LongTextDelegate(),
        LoremIpsumGenerator::class to LoremIpsumGeneratorDelegate(),
        ItemList::class to ItemListDelegate<FinchListItem>(),
        DesignOverlay::class to DesignOverlayDelegate(),
        KeyValueList::class to KeyValueListDelegate(),
        MultipleSelectionList::class to MultipleSelectionListDelegate<FinchListItem>(),
        NetworkLogs::class to NetworkLogsDelegate(),
        Padding::class to PaddingDelegate(),
        ScreenCaptureToolbox::class to ScreenCaptureToolboxDelegate(),
        ScreenRecording::class to ScreenRecordingDelegate(),
        Screenshot::class to ScreenshotDelegate(),
        SingleSelectionList::class to SingleSelectionListDelegate<FinchListItem>(),
        Slider::class to SliderDelegate(),
        Switch::class to SwitchDelegate(),
        Label::class to LabelDelegate(),
        TextInput::class to TextInputDelegate()
    )

    private val persistableComponents
        get() = synchronized(components) {
            components.filterIsInstance<ValueWrapperComponent<*, *>>().toList()
        }

    fun setupRecyclerView(recyclerView: GestureBlockingRecyclerView) = recyclerView.run {
        adapter = cellAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(recyclerView.context)
        shouldBlockGestures = { this@ListManager.shouldBlockGestures }
    }

    fun setComponents(newComponents: List<Component<*>>, onContentsChanged: () -> Unit) {
        GlobalScope.launch { setComponentsInternal(newComponents, onContentsChanged) }
    }

    fun addComponents(
        newComponents: List<Component<*>>,
        position: Position,
        lifecycleOwner: LifecycleOwner?,
        onContentsChanged: () -> Unit
    ) {
        GlobalScope.launch(if (lifecycleOwner == null) Dispatchers.Default else Dispatchers.Main) {
            removeComponentsInternal(listOf(HINT_COMPONENT_ID)) {}
            addComponentsInternal(newComponents, position, lifecycleOwner, onContentsChanged)
        }
    }

    fun removeComponents(ids: List<String>, onContentsChanged: () -> Unit = {}) {
        GlobalScope.launch { removeComponentsInternal(ids, onContentsChanged) }
    }

    fun refreshCells(onContentsChanged: () -> Unit) {
        GlobalScope.launch { refreshCellsInternal(onContentsChanged) }
    }

    fun contains(id: String) = synchronized(components) { components.any { it.id == id } }

    fun applyPendingChanges() {
        persistableComponents.forEach { it.applyPendingChanges(FinchCore.implementation) }
        FinchCore.implementation.refresh()
    }

    fun resetPendingChanges() {
        persistableComponents.forEach { it.resetPendingChanges(FinchCore.implementation) }
        FinchCore.implementation.refresh()
    }

    @Suppress("UNCHECKED_CAST")
    fun <C : Component<*>> findComponent(id: String): C? =
        synchronized(components) { components.firstOrNull { it.id == id } as? C? }

    @Suppress("UNCHECKED_CAST")
    fun <C : Component<C>> findComponentDelegate(type: KClass<out C>) =
        synchronized(components) { componentDelegates[type] as? Component.Delegate<C>? }

    private suspend fun setComponentsInternal(
        newComponents: List<Component<*>>,
        onContentsChanged: () -> Unit
    ) = withContext(componentManagerContext) {
        synchronized(components) {
            components.clear()
            components.addAll(newComponents.distinctBy { it.id })
        }
        refreshCellsInternal(onContentsChanged)
    }

    @Suppress("unused")
    private suspend fun addComponentsInternal(
        newComponents: List<Component<*>>,
        position: Position,
        lifecycleOwner: LifecycleOwner?,
        onContentsChanged: () -> Unit
    ) =
        lifecycleOwner?.lifecycle?.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                GlobalScope.launch {
                    addComponentsInternal(
                        newComponents,
                        position,
                        onContentsChanged
                    )
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                GlobalScope.launch {
                    removeComponents(
                        newComponents.map { it.id },
                        onContentsChanged
                    )
                }
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        }) ?: addComponentsInternal(newComponents, position, onContentsChanged)

    private suspend fun addComponentsInternal(
        newComponents: List<Component<*>>,
        position: Position,
        onContentsChanged: () -> Unit
    ) = withContext(componentManagerContext) {
        synchronized(components) {
            components.apply {
                var newIndex = 0
                newComponents.distinctBy { it.id }.forEach { component ->
                    indexOfFirst { it.id == component.id }.also { currentIndex ->
                        if (currentIndex != -1) {
                            removeAt(currentIndex)
                            add(currentIndex, component)
                        } else {
                            when (position) {
                                Position.Bottom -> add(component)
                                Position.Top -> add(newIndex++, component)
                                is Position.Below -> {
                                    indexOfFirst { it.id == position.id }.also { referencePosition ->
                                        if (referencePosition == -1) {
                                            add(component)
                                        } else {
                                            add(referencePosition + 1 + newIndex++, component)
                                        }
                                    }
                                }
                                is Position.Above -> {
                                    indexOfFirst { it.id == position.id }.also { referencePosition ->
                                        if (referencePosition == -1) {
                                            add(newIndex++, component)
                                        } else {
                                            add(referencePosition, component)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        refreshCellsInternal(onContentsChanged)
    }

    private suspend fun removeComponentsInternal(ids: List<String>, onContentsChanged: () -> Unit) =
        withContext(componentManagerContext) {
            synchronized(components) {
                components.removeAll { ids.contains(it.id) }
            }
            refreshCellsInternal(onContentsChanged)
        }

    private suspend fun refreshCellsInternal(onContentsChanged: () -> Unit) =
        withContext(componentManagerContext) {
            val newCells = synchronized(components) {
                components.flatMap { component ->
                    (componentDelegates[component::class] ?: (component.createComponentDelegate()
                        .also {
                            componentDelegates[component::class] = it
                        })).forceCreateCells(component)
                }
            }
            if (cellAdapter.itemCount > 0 && cellAdapter.itemCount != newCells.size) {
                shouldBlockGestures = true
            }
            cellAdapter.submitList(newCells) {
                onContentsChanged()
                countDownJob?.cancel()
                countDownJob = GlobalScope.launch {
                    delay(300)
                    shouldBlockGestures = false
                }
            }
        }

    companion object {
        private const val HINT_COMPONENT_ID = "finchHintComponent"
    }
}
