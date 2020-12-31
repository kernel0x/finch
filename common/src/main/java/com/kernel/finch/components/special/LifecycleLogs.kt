package com.kernel.finch.components.special

import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text
import java.text.SimpleDateFormat
import java.util.*

data class LifecycleLogs(
    val title: Text = Text.CharSequence(DEFAULT_TITLE),
    val eventTypes: List<EventType> = DEFAULT_EVENT_TYPES,
    val shouldDisplayFullNames: Boolean = DEFAULT_SHOULD_DISPLAY_FULL_NAMES,
    val maxItemCount: Int = DEFAULT_MAX_ITEM_COUNT,
    val timeFormatter: ((Long) -> CharSequence) = { DEFAULT_DATE_FORMAT.format(it) },
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY
) : ExpandableComponent<LifecycleLogs> {

    override val id = ID

    override fun getHeaderTitle(finch: Finch) = title

    enum class EventType(val formattedName: String) {
        ON_CREATE("onCreate()"),
        ON_START("onStart()"),
        ON_RESUME("onResume()"),
        ON_SAVE_INSTANCE_STATE("onSaveInstanceState()"),
        ON_PAUSE("onPause()"),
        ON_STOP("onStop()"),
        ON_DESTROY("onDestroy()"),
        FRAGMENT_ON_ATTACH("onAttach()"),
        FRAGMENT_ON_ACTIVITY_CREATED("onActivityCreated()"),
        FRAGMENT_ON_VIEW_CREATED("onCreateView()"),
        FRAGMENT_ON_VIEW_DESTROYED("onDestroyView()"),
        FRAGMENT_ON_DETACH("onDetach()")
    }

    companion object {
        const val ID = "lifecycleLogList"
        private const val DEFAULT_TITLE = "Lifecycle logs"
        private val DEFAULT_EVENT_TYPES = EventType.values().toList()
        private const val DEFAULT_SHOULD_DISPLAY_FULL_NAMES = false
        private const val DEFAULT_MAX_ITEM_COUNT = 20
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
        private val DEFAULT_DATE_FORMAT by lazy {
            SimpleDateFormat(
                Finch.LOG_TIME_FORMAT,
                Locale.ENGLISH
            )
        }
    }
}
