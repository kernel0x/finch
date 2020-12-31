package com.kernel.finch.common.contracts.component

import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<T : Cell<T>>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: T)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Suppress("UNCHECKED_CAST")
    fun forceBind(model: Cell<*>) = try {
        bind(model as T)
    } catch (_: ClassCastException) {
    }

    abstract class Delegate<T : Cell<T>> {

        abstract fun createViewHolder(parent: ViewGroup): ViewHolder<T>
    }
}
