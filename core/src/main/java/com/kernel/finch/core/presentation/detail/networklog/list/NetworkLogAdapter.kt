package com.kernel.finch.core.presentation.detail.networklog.list

import android.view.ViewGroup
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.core.presentation.detail.networklog.NetworkLogListFragment

internal class NetworkLogAdapter(private val listener: NetworkLogListFragment.OnListFragmentInteractionListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<NetworkLogViewHolder>() {

    private var data: List<NetworkLogEntity> = ArrayList()

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: NetworkLogViewHolder, position: Int) =
        holder.bind(data[position], listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkLogViewHolder =
        NetworkLogViewHolder(parent)

    fun setData(data: List<NetworkLogEntity>) {
        this.data = data
        notifyDataSetChanged()
    }
}
