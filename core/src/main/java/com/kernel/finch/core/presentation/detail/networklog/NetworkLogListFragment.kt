package com.kernel.finch.core.presentation.detail.networklog

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kernel.finch.FinchCore
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.core.R
import com.kernel.finch.core.data.db.FinchDatabase
import com.kernel.finch.core.data.db.NetworkLogDao
import com.kernel.finch.core.manager.NotificationManager
import com.kernel.finch.core.presentation.detail.networklog.list.NetworkLogAdapter
import com.kernel.finch.utils.consume

internal class NetworkLogListFragment : Fragment(), SearchView.OnQueryTextListener {

    private var currentFilter: String = ""
    private lateinit var listener: OnListFragmentInteractionListener
    private lateinit var networkLogDao: NetworkLogDao
    private lateinit var adapter: NetworkLogAdapter

    private val limit by lazy { FinchCore.implementation.configuration.maxSize }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        networkLogDao = FinchDatabase.getInstance(requireContext()).networkLog().also {
            it.getAll(limit = limit).observe(this, { list ->
                if (list != null) {
                    adapter.setData(list)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.finch_fragment_network_log_list, container, false).also {
        if (it is RecyclerView) {
            adapter = NetworkLogAdapter(listener)
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.finch_main, menu)
        val searchMenuItem = menu.findItem(R.id.finch_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.finch_delete -> consume {
                AsyncTask.execute {
                    networkLogDao.deleteAll()
                }
                NotificationManager.clearBuffer()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        currentFilter = newText
        if (currentFilter.isNotEmpty()) {
            if (TextUtils.isDigitsOnly(currentFilter)) {
                networkLogDao
                    .getAll(Integer.parseInt(currentFilter)).observe(this, { list ->
                        if (list != null) {
                            adapter.setData(list)
                        }
                    })
            } else {
                networkLogDao
                    .getAll(currentFilter, limit = limit)
                    .observe(this, { list ->
                        if (list != null) {
                            adapter.setData(list)
                        }
                    })
            }
        } else {
            networkLogDao.getAll(limit = limit)
                .observe(this, { list ->
                    if (list != null) {
                        adapter.setData(list)
                    }
                })
        }
        return true
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(view: View, item: NetworkLogEntity)
    }

    companion object {
        fun newInstance(): NetworkLogListFragment {
            return NetworkLogListFragment()
        }
    }
}
