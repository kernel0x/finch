package com.kernel.finch.core.presentation.detail.networklog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.core.R
import com.kernel.finch.core.util.formatDateTime

internal class NetworkLogOverviewFragment : Fragment(), NetworkLogFragment {

    private var networkLog: NetworkLogEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        LayoutInflater.from(context)
            .inflate(R.layout.finch_fragment_network_log_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
    }

    override fun networkLogUpdated(networkLog: NetworkLogEntity) {
        this.networkLog = networkLog
        update()
    }

    private fun update() {
        view?.run {
            networkLog?.also {
                findViewById<TextView>(R.id.finch_url).text = it.url
                findViewById<TextView>(R.id.finch_method).text = it.method
                findViewById<TextView>(R.id.finch_protocol).text = it.protocol
                findViewById<TextView>(R.id.finch_status).text = it.getStatus().toString()
                findViewById<TextView>(R.id.finch_response).text = it.getResponseSummaryText()
                findViewById<TextView>(R.id.finch_ssl).setText(if (it.isSsl()) R.string.finch_yes else R.string.finch_no)
                findViewById<TextView>(R.id.finch_request_time).text =
                    formatDateTime(it.requestDate)
                findViewById<TextView>(R.id.finch_response_time).text =
                    formatDateTime(it.responseDate)
                findViewById<TextView>(R.id.finch_duration).text = it.getDurationString()
                findViewById<TextView>(R.id.finch_request_size).text = it.getRequestSizeString()
                findViewById<TextView>(R.id.finch_response_size).text = it.getResponseSizeString()
                findViewById<TextView>(R.id.finch_total_size).text = it.getTotalSizeString()
            }
        }
    }
}
