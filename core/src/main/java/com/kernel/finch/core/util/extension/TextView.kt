package com.kernel.finch.core.util.extension

import android.widget.TextView
import com.kernel.finch.common.models.Text

internal fun TextView.setText(text: Text?) = text?.let {
    this.text = context.text(text)
} ?: Unit
