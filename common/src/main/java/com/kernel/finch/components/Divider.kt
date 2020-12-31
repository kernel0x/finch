package com.kernel.finch.components

import com.kernel.finch.common.contracts.component.Component

data class Divider(
    override val id: String = Component.randomId
) : Component<Divider>
