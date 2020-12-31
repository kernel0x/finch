package com.kernel.finch.core.list.delegates.shared

import androidx.annotation.CallSuper
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.ValueWrapperComponent

internal abstract class ValueWrapperComponentDelegate<T, C : ValueWrapperComponent<T, C>> :
    ValueWrapperComponent.Delegate<T, C> {

    private var hasCalledListenerForTheFirstTime = false
    private val pendingUpdates = mutableListOf<PendingChangeEvent<T>>()
    private val uiValues = mutableMapOf<kotlin.String, T>()

    fun getUiValue(component: C) = if (component.shouldRequireConfirmation) (uiValues[component.id]
        ?: getCurrentValue(component)) else getCurrentValue(component)

    fun setUiValue(component: C, newValue: T) {
        if (component.shouldRequireConfirmation) {
            pendingUpdates.removeAll { it.componentId == component.id }
            if (getCurrentValue(component) == newValue) {
                uiValues.remove(component.id)
            } else {
                uiValues[component.id] = newValue
                pendingUpdates.add(
                    PendingChangeEvent(
                        componentId = component.id,
                        pendingValue = newValue
                    )
                )
            }
            FinchCore.implementation.refresh()
        } else if (hasValueChanged(newValue, component)) {
            setCurrentValue(component, newValue)
        }
    }

    protected fun hasValueChanged(newValue: T, component: C) = newValue != getUiValue(component)

    override fun hasPendingChanges(component: C) =
        component.shouldRequireConfirmation && pendingUpdates.any { it.componentId == component.id }

    override fun applyPendingChanges(component: C) {
        pendingUpdates.indexOfFirst { it.componentId == component.id }.let { index ->
            if (index != -1) {
                uiValues.remove(component.id)
                setCurrentValue(component, pendingUpdates[index].pendingValue)
                pendingUpdates.removeAt(index)
            }
        }
    }

    override fun resetPendingChanges(component: C) {
        pendingUpdates.removeAll { it.componentId == component.id }
        uiValues.remove(component.id)
        getCurrentValue(component)?.let { setCurrentValue(component, it) }
    }

    protected fun callListenerForTheFirstTimeIfNeeded(component: C, value: T) {
        if (component.isValuePersisted && !hasCalledListenerForTheFirstTime && FinchCore.implementation.currentActivity != null) {
            hasCalledListenerForTheFirstTime = true
            callOnValueChanged(component, value)
        }
    }

    @CallSuper
    protected open fun callOnValueChanged(component: C, newValue: T) =
        component.onValueChanged(newValue)

    abstract class Boolean<C : ValueWrapperComponent<kotlin.Boolean, C>> :
        ValueWrapperComponentDelegate<kotlin.Boolean, C>() {

        final override fun getCurrentValue(component: C) = if (component.isValuePersisted) {
            (FinchCore.implementation.localStorageManager.booleans[component.id]
                ?: component.initialValue).also { value ->
                callListenerForTheFirstTimeIfNeeded(component, value)
            }
        } else {
            FinchCore.implementation.memoryStorageManager.booleans[component.id]
                ?: component.initialValue
        }

        final override fun setCurrentValue(component: C, newValue: kotlin.Boolean) {
            if (hasValueChanged(newValue, component)) {
                if (component.isValuePersisted) {
                    FinchCore.implementation.localStorageManager.booleans[component.id] = newValue
                } else {
                    FinchCore.implementation.memoryStorageManager.booleans[component.id] = newValue
                }
                FinchCore.implementation.refresh()
                callOnValueChanged(component, newValue)
            }
        }
    }

    abstract class Integer<C : ValueWrapperComponent<Int, C>> :
        ValueWrapperComponentDelegate<Int, C>() {

        final override fun getCurrentValue(component: C) = if (component.isValuePersisted) {
            (FinchCore.implementation.localStorageManager.integers[component.id]
                ?: component.initialValue).also { value ->
                callListenerForTheFirstTimeIfNeeded(component, value)
            }
        } else {
            FinchCore.implementation.memoryStorageManager.integers[component.id]
                ?: component.initialValue
        }

        final override fun setCurrentValue(component: C, newValue: Int) {
            if (hasValueChanged(newValue, component)) {
                if (component.isValuePersisted) {
                    FinchCore.implementation.localStorageManager.integers[component.id] = newValue
                } else {
                    FinchCore.implementation.memoryStorageManager.integers[component.id] = newValue
                }
                FinchCore.implementation.refresh()
                callOnValueChanged(component, newValue)
            }
        }
    }

    abstract class String<M : ValueWrapperComponent<kotlin.String, M>> :
        ValueWrapperComponentDelegate<kotlin.String, M>() {

        final override fun getCurrentValue(component: M) = if (component.isValuePersisted) {
            (FinchCore.implementation.localStorageManager.strings[component.id]
                ?: component.initialValue).also { value ->
                callListenerForTheFirstTimeIfNeeded(component, value)
            }
        } else {
            FinchCore.implementation.memoryStorageManager.strings[component.id]
                ?: component.initialValue
        }

        final override fun setCurrentValue(component: M, newValue: kotlin.String) {
            if (hasValueChanged(newValue, component)) {
                if (component.isValuePersisted) {
                    FinchCore.implementation.localStorageManager.strings[component.id] = newValue
                } else {
                    FinchCore.implementation.memoryStorageManager.strings[component.id] = newValue
                }
                FinchCore.implementation.refresh()
                callOnValueChanged(component, newValue)
            }
        }
    }

    abstract class StringSet<C : ValueWrapperComponent<Set<kotlin.String>, C>> :
        ValueWrapperComponentDelegate<Set<kotlin.String>, C>() {

        final override fun getCurrentValue(component: C) = if (component.isValuePersisted) {
            (FinchCore.implementation.localStorageManager.stringSets[component.id]
                ?: component.initialValue).also { value ->
                callListenerForTheFirstTimeIfNeeded(component, value)
            }
        } else {
            FinchCore.implementation.memoryStorageManager.stringSets[component.id]
                ?: component.initialValue
        }

        final override fun setCurrentValue(component: C, newValue: Set<kotlin.String>) {
            if (hasValueChanged(newValue, component)) {
                if (component.isValuePersisted) {
                    FinchCore.implementation.localStorageManager.stringSets[component.id] = newValue
                } else {
                    FinchCore.implementation.memoryStorageManager.stringSets[component.id] =
                        newValue
                }
                FinchCore.implementation.refresh()
                callOnValueChanged(component, newValue)
            }
        }
    }

    data class PendingChangeEvent<T>(
        val componentId: kotlin.String,
        val pendingValue: T
    )
}
