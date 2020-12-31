package com.kernel.finch.components

import com.kernel.finch.common.contracts.component.Component

data class ProgressBar(
    override val id: String = Component.randomId
) : Component<ProgressBar>
