package com.kernel.finch

import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.core.FinchImpl
import com.kernel.finch.core.manager.UiManager

@Suppress("unused")
object Finch : Finch by FinchImpl(object : UiManager {})
