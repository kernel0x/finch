package com.kernel.finch.common.contracts.component

interface Cell<T : Cell<T>> {

    val id: String

    fun createViewHolderDelegate(): ViewHolder.Delegate<T>

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}
