package com.kernel.finch.components

import com.kernel.finch.common.contracts.component.Component

data class Padding(
    override val id: String = Component.randomId
) : Component<Padding>
