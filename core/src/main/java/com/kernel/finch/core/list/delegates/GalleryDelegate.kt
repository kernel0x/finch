package com.kernel.finch.core.list.delegates

import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.components.special.Gallery
import com.kernel.finch.core.util.createTextComponentFromType

internal class GalleryDelegate : Component.Delegate<Gallery> {

    override fun createCells(component: Gallery): List<Cell<*>> = listOf(
        createTextComponentFromType(
            type = component.type,
            id = component.id,
            text = component.text,
            isEnabled = component.isEnabled,
            icon = component.icon,
            onItemSelected = {
                component.onButtonPressed()
                FinchCore.implementation.openGallery()
            }
        )
    )
}
