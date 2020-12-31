package com.kernel.finch.common.contracts.component

import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.models.Text

interface ExpandableComponent<M : Component<M>> : Component<M> {

    val isExpandedInitially: Boolean

    fun getHeaderTitle(finch: Finch): Text
}
