package com.kernel.finch.common.contracts.component

import androidx.annotation.RestrictTo
import java.util.*

interface Component<C : Component<C>> {

    val id: String

    fun createComponentDelegate(): Delegate<C> =
        throw IllegalStateException("Built-in Components should never create their own Delegates.")

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    interface Delegate<C : Component<C>> {

        fun createCells(component: C): List<Cell<*>>

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @Suppress("UNCHECKED_CAST")
        fun forceCreateCells(component: Component<*>) = createCells(component as C)
    }

    companion object {
        val randomId get() = UUID.randomUUID().toString()
    }
}
