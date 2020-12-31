package com.kernel.finch.core.presentation.detail.networklog.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.core.R
import com.kernel.finch.core.presentation.detail.networklog.NetworkLogListFragment
import com.kernel.finch.core.util.formatTime

internal class NetworkLogViewHolder(container: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(container.context)
        .inflate(R.layout.finch_item_network_log, container, false)
) {

    private val codeTextView = itemView.findViewById<TextView>(R.id.finch_code)
    private val pathTextView = itemView.findViewById<TextView>(R.id.finch_path)
    private val hostTextView = itemView.findViewById<TextView>(R.id.finch_host)
    private val startTextView = itemView.findViewById<TextView>(R.id.finch_start)
    private val sslImageView = itemView.findViewById<ImageView>(R.id.finch_ssl)
    private val durationTextView = itemView.findViewById<TextView>(R.id.finch_duration)
    private val sizeTextView = itemView.findViewById<TextView>(R.id.finch_size)

    @SuppressLint("SetTextI18n")
    fun bind(
        networkLog: NetworkLogEntity,
        listener: NetworkLogListFragment.OnListFragmentInteractionListener
    ) {
        with(itemView) {
            pathTextView.text = networkLog.method + " " + networkLog.path
            hostTextView.text = networkLog.host
            startTextView.text = formatTime(networkLog.requestDate)
            sslImageView.visibility = if (networkLog.isSsl()) View.VISIBLE else View.GONE
            if (networkLog.getStatus() == NetworkLogEntity.Status.COMPLETE) {
                codeTextView.text = networkLog.responseCode.toString()
                durationTextView.text = networkLog.getDurationString()
                sizeTextView.text = networkLog.getTotalSizeString()
            } else {
                codeTextView.text = null
                durationTextView.text = null
                sizeTextView.text = null
            }
            if (networkLog.getStatus() == NetworkLogEntity.Status.FAIL) {
                codeTextView.text = "!!!"
            }
            setStatusColor(this, networkLog)
            setOnClickListener {
                listener.onListFragmentInteraction(pathTextView, networkLog)
            }
        }
    }

    private fun setStatusColor(view: View, networkLog: NetworkLogEntity) {
        val color: Int = when {
            networkLog.getStatus() == NetworkLogEntity.Status.FAIL -> ContextCompat.getColor(
                view.context,
                R.color.finch_status_error
            )
            networkLog.getStatus() == NetworkLogEntity.Status.PROGRESS -> ContextCompat.getColor(
                view.context,
                R.color.finch_status_requested
            )
            networkLog.responseCode >= 500 -> ContextCompat.getColor(
                view.context,
                R.color.finch_status_500
            )
            networkLog.responseCode >= 400 -> ContextCompat.getColor(
                view.context,
                R.color.finch_status_400
            )
            networkLog.responseCode >= 300 -> ContextCompat.getColor(
                view.context,
                R.color.finch_status_300
            )
            else -> ContextCompat.getColor(view.context, R.color.finch_status_default)
        }
        codeTextView.setTextColor(color)
        pathTextView.setTextColor(color)
    }
}
