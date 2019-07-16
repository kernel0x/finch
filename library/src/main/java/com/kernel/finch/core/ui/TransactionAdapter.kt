package com.kernel.finch.core.ui

import android.view.ViewGroup
import com.kernel.finch.core.data.models.TransactionHttpEntity
import com.kernel.finch.core.ui.TransactionListFragment.OnListFragmentInteractionListener

class TransactionAdapter(private val listener: OnListFragmentInteractionListener) : androidx.recyclerview.widget.RecyclerView.Adapter<TransactionViewHolder>() {

    private var data: List<TransactionHttpEntity> = ArrayList()

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) = holder.bind(data[position], listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder = TransactionViewHolder(parent)

    fun setData(data: List<TransactionHttpEntity>) {
        this.data = data
        notifyDataSetChanged()
    }
}
