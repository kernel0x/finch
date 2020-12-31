package com.kernel.finch.common.contracts.component

import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.models.Text

interface ValueWrapperComponent<T, C : Component<C>> : Component<C> {

    val initialValue: T

    val text: (T) -> Text

    val isValuePersisted: Boolean

    val isEnabled: Boolean

    val shouldRequireConfirmation: Boolean

    val onValueChanged: (newValue: T) -> Unit

    override fun createComponentDelegate(): Delegate<T, C> =
        throw IllegalStateException("Built-in Components should never create their own Delegates.")

    @Suppress("UNCHECKED_CAST")
    fun hasPendingChanges(finch: Finch): Boolean =
        (finch.delegateFor((this as C)::class) as? Delegate<T, C>?)?.hasPendingChanges(this as C) == true

    @Suppress("UNCHECKED_CAST")
    fun applyPendingChanges(finch: Finch) =
        (finch.delegateFor((this as C)::class) as? Delegate<T, C>?)?.applyPendingChanges(this as C)

    @Suppress("UNCHECKED_CAST")
    fun resetPendingChanges(finch: Finch) =
        (finch.delegateFor((this as C)::class) as? Delegate<T, C>?)?.resetPendingChanges(this as C)

    @Suppress("UNCHECKED_CAST")
    fun getCurrentValue(finch: Finch): T? =
        (finch.delegateFor((this as C)::class) as? Delegate<T, C>?)?.getCurrentValue(this as C)

    @Suppress("UNCHECKED_CAST")
    fun setCurrentValue(finch: Finch, newValue: T) =
        (finch.delegateFor((this as C)::class) as? Delegate<T, C>?)?.setCurrentValue(
            this as C,
            newValue
        )

    interface Delegate<T, C : Component<C>> : Component.Delegate<C> {

        fun hasPendingChanges(component: C): Boolean

        fun applyPendingChanges(component: C)

        fun resetPendingChanges(component: C)

        fun getCurrentValue(component: C): T

        fun setCurrentValue(component: C, newValue: T)
    }
}
