package com.kernel.finch

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.kernel.finch.core.presentation.InternalDebugMenuView
import com.kernel.finch.core.util.extension.applyTheme

@SuppressLint("SetTextI18n", "unused")
//TODO: Notify the visibility listeners when this view is attached / detached.
class DebugMenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        addView(
            InternalDebugMenuView(context.applyTheme()),
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }
}
