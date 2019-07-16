package com.kernel.finch.core.ui

import com.kernel.finch.core.data.models.TransactionHttpEntity

interface TransactionFragment {
    fun transactionUpdated(transaction: TransactionHttpEntity)
}