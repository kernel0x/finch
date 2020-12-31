package com.kernel.finch.components.special

import androidx.annotation.StringRes
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.models.Text

@Suppress("Unused")
data class Header(
    val title: Text,
    val subtitle: Text? = DEFAULT_SUBTITLE?.let { Text.CharSequence(it) },
    val text: Text? = DEFAULT_TEXT?.let { Text.CharSequence(it) }
) : Component<Header> {

    constructor(
        title: CharSequence,
        subtitle: CharSequence? = DEFAULT_SUBTITLE,
        text: CharSequence? = DEFAULT_TEXT
    ) : this(
        title = Text.CharSequence(title),
        subtitle = subtitle?.let { Text.CharSequence(it) },
        text = text?.let { Text.CharSequence(it) }
    )

    constructor(
        @StringRes title: Int,
        @StringRes subtitle: Int? = DEFAULT_SUBTITLE_INT,
        @StringRes text: Int? = DEFAULT_TEXT_INT
    ) : this(
        title = Text.ResourceId(title),
        subtitle = subtitle?.let { Text.ResourceId(it) },
        text = text?.let { Text.ResourceId(it) }
    )

    override val id: String = ID

    companion object {
        const val ID = "header"
        private val DEFAULT_SUBTITLE: CharSequence? = null
        private val DEFAULT_SUBTITLE_INT: Int? = null
        private val DEFAULT_TEXT: CharSequence? = null
        private val DEFAULT_TEXT_INT: Int? = null
    }
}
