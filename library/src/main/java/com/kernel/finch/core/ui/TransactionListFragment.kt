package com.kernel.finch.core.ui

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kernel.finch.R
import com.kernel.finch.core.data.FinchDatabase
import com.kernel.finch.core.data.models.TransactionHttpEntity
import com.kernel.finch.core.helpers.NotificationHelper

class TransactionListFragment : Fragment(), SearchView.OnQueryTextListener {

    private var currentFilter: String? = null
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        FinchDatabase.getInstance(context!!).transactionHttp().getAll().observe(this, Observer { list ->
            if (list != null) {
                adapter.setData(list)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.lib_finch_fragment_transaction_list, container, false)
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            adapter = TransactionAdapter(listener!!)
            view.adapter = adapter
        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.lib_finch_main, menu)
        val searchMenuItem = menu!!.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when {
            item!!.itemId == R.id.clear -> {
                AsyncTask.execute { FinchDatabase.getInstance(context!!).transactionHttp().deleteAll() }
                NotificationHelper.clearBuffer()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        currentFilter = newText
        if (!TextUtils.isEmpty(currentFilter)) {
            if (TextUtils.isDigitsOnly(currentFilter)) {
                FinchDatabase.getInstance(context!!).transactionHttp().getAll(Integer.parseInt(currentFilter!!)).observe(this, Observer { list ->
                    if (list != null) {
                        adapter.setData(list)
                    }
                })
            } else {
                FinchDatabase.getInstance(context!!).transactionHttp().getAll(currentFilter!!).observe(this, Observer { list ->
                    if (list != null) {
                        adapter.setData(list)
                    }
                })
            }
        } else {
            FinchDatabase.getInstance(context!!).transactionHttp().getAll().observe(this, Observer { list ->
                if (list != null) {
                    adapter.setData(list)
                }
            })
        }
        return true
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(view: View, item: TransactionHttpEntity)
    }

    companion object {
        fun newInstance(): TransactionListFragment {
            return TransactionListFragment()
        }
    }
}
