package com.kernel.finch.core.list.delegates

import android.animation.ValueAnimator
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.special.AnimationSpeed
import com.kernel.finch.core.list.cells.SwitchCell
import com.kernel.finch.core.list.delegates.shared.ValueWrapperComponentDelegate

internal class AnimationSpeedDelegate :
    ValueWrapperComponentDelegate.Boolean<AnimationSpeed>() {

    override fun createCells(component: AnimationSpeed): List<Cell<*>> =
        getCurrentValue(component).let { currentValue ->
            listOf(
                SwitchCell(
                    id = component.id,
                    text = component.text(currentValue),
                    isChecked = currentValue,
                    isEnabled = component.isEnabled,
                    onValueChanged = { newValue -> setCurrentValue(component, newValue) }
                )
            )
        }

    override fun callOnValueChanged(
        component: AnimationSpeed,
        newValue: kotlin.Boolean
    ) {
        try {
            ValueAnimator::class.java.methods.firstOrNull { it.name == "setDurationScale" }
                ?.invoke(null, if (newValue) component.multiplier else 1f)
        } catch (_: Throwable) {
        }
        super.callOnValueChanged(component, newValue)
    }
}
