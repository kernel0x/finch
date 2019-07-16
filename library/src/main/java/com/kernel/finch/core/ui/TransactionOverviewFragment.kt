package com.kernel.finch.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kernel.finch.R
import com.kernel.finch.core.data.models.TransactionHttpEntity
import kotlinx.android.synthetic.main.lib_finch_fragment_transaction_overview.*

class TransactionOverviewFragment : Fragment(), TransactionFragment {

    private var transaction: TransactionHttpEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(context).inflate(R.layout.lib_finch_fragment_transaction_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
    }

    override fun transactionUpdated(transaction: TransactionHttpEntity) {
        this.transaction = transaction
        update()
    }

    private fun update() {
        if (isAdded && transaction != null) {
            viewUrl.text = transaction!!.url
            viewMethod.text = transaction!!.method
            viewProtocol.text = transaction!!.protocol
            viewStatus.text = transaction!!.getStatus().toString()
            viewResponse.text = transaction!!.getResponseSummaryText()
            viewSsl.setText(if (transaction!!.isSsl()) R.string.lib_finch_yes else R.string.lib_finch_no)
            viewRequestTime.text = transaction!!.requestDate
            viewResponseTime.text = transaction!!.responseDate
            viewDuration.text = transaction!!.getDurationString()
            viewRequestSize.text = transaction!!.getRequestSizeString()
            viewResponseSize.text = transaction!!.getResponseSizeString()
            viewTotalSize.text = transaction!!.getTotalSizeString()
        }
    }
}
