package com.kernel.finch.common.models

import androidx.annotation.StringRes

sealed class Text {

    data class CharSequence(val charSequence: kotlin.CharSequence) : Text()

    data class ResourceId(@StringRes val resId: Int) : Text()

    var suffix: kotlin.CharSequence = ""
        private set

    fun with(suffix: kotlin.CharSequence) = this.also {
        this.suffix = suffix
    }
}
