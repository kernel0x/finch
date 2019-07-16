package com.kernel.finch.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kernel.finch.R
import com.kernel.finch.core.data.models.TransactionHttpEntity
import kotlinx.android.synthetic.main.lib_finch_list_item_transaction.view.*

class TransactionViewHolder(container: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(container.context).inflate(R.layout.lib_finch_list_item_transaction, container, false)) {

    @SuppressLint("SetTextI18n")
    fun bind(transaction: TransactionHttpEntity, listener: TransactionListFragment.OnListFragmentInteractionListener) {
        with(itemView) {
            viewPath.text = transaction.method + " " + transaction.path
            viewHost.text = transaction.host
            viewStart.text = transaction.getRequestStartTimeString()
            viewSsl.visibility = if (transaction.isSsl()) View.VISIBLE else View.GONE
            if (transaction.getStatus() == TransactionHttpEntity.Status.COMPLETE) {
                viewCode.text = transaction.responseCode.toString()
                viewDuration.text = transaction.getDurationString()
                viewSize.text = transaction.getTotalSizeString()
            } else {
                viewCode.text = null
                viewDuration.text = null
                viewSize.text = null
            }
            if (transaction.getStatus() == TransactionHttpEntity.Status.FAIL) {
                viewCode.text = "!!!"
            }
            setStatusColor(this, transaction)
            setOnClickListener {
                listener.onListFragmentInteraction(viewPath, transaction)
            }
        }
    }

    private fun setStatusColor(view: View, transaction: TransactionHttpEntity) {
        val color: Int = when {
            transaction.getStatus() == TransactionHttpEntity.Status.FAIL -> ContextCompat.getColor(view.context, R.color.lib_finch_status_error)
            transaction.getStatus() == TransactionHttpEntity.Status.PROGRESS -> ContextCompat.getColor(view.context, R.color.lib_finch_status_requested)
            transaction.responseCode >= 500 -> ContextCompat.getColor(view.context, R.color.lib_finch_status_500)
            transaction.responseCode >= 400 -> ContextCompat.getColor(view.context, R.color.lib_finch_status_400)
            transaction.responseCode >= 300 -> ContextCompat.getColor(view.context, R.color.lib_finch_status_300)
            else -> ContextCompat.getColor(view.context, R.color.lib_finch_status_default)
        }
        view.viewCode.setTextColor(color)
        view.viewPath.setTextColor(color)
    }
}