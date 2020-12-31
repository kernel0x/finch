package com.kernel.finch.common.contracts

import com.kernel.finch.common.models.Text

interface FinchListItem {

    val title: Text

    val id: String
        get() = when (val title = title) {
            is Text.CharSequence -> title.charSequence.toString()
            is Text.ResourceId -> title.resId.toString()
        }

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}
