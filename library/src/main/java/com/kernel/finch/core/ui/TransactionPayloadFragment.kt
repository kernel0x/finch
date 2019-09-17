package com.kernel.finch.core.ui

import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.kernel.finch.R
import com.kernel.finch.core.data.models.MediaType
import com.kernel.finch.core.data.models.TransactionHttpEntity
import kotlinx.android.synthetic.main.lib_finch_fragment_transaction_payload.*

class TransactionPayloadFragment : Fragment(), TransactionFragment, SearchView.OnQueryTextListener {

    private var type: Int = 0
    private var transaction: TransactionHttpEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments!!.getInt(ARG_TYPE)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(context).inflate(R.layout.lib_finch_fragment_transaction_payload, container, false)

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
            when (type) {
                TYPE_REQUEST -> setText(transaction!!.getRequestHeadersString(true),
                        transaction!!.getFormattedRequestBody(), transaction!!.requestBodyIsPlainText)
                TYPE_RESPONSE -> setText(transaction!!.getResponseHeadersString(true),
                        transaction!!.getFormattedResponseBody(), transaction!!.responseBodyIsPlainText)
            }
        }
    }

    private fun setText(headersString: String, bodyString: String, isPlainText: Boolean) {
        viewHeaders.visibility = if (TextUtils.isEmpty(headersString)) View.GONE else View.VISIBLE
        viewHeaders.text = Html.fromHtml(headersString)
        if (!isPlainText) {
            viewBody.text = getString(R.string.lib_finch_body_omitted)
        } else {
            viewBody.text = bodyString
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val searchMenuItem = menu!!.findItem(R.id.search)
        searchMenuItem.isVisible = true
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(true)

        menu.findItem(R.id.share_json)?.isVisible = transaction?.responseContentType == MediaType.APPLICATION_JSON

        super.onPrepareOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        var fullText = viewBody.text.toString()
        if (newText.isEmpty() || !fullText.contains(newText) || TextUtils.isEmpty(newText)) {
            update()
        } else {
            val indexOfCriteria = fullText.indexOf(newText)
            val lineNumber = viewBody.layout.getLineForOffset(indexOfCriteria)
            val highlighted = "<font color='red'>$newText</font>"
            fullText = fullText.replace(newText, highlighted)
            viewBody.text = Html.fromHtml(fullText)
            viewScroll.scrollTo(0, viewBody.layout.getLineTop(lineNumber))
        }
        return true
    }

    companion object {
        const val TYPE_REQUEST = 0
        const val TYPE_RESPONSE = 1

        private const val ARG_TYPE = "type"

        fun newInstance(type: Int): TransactionPayloadFragment {
            val fragment = TransactionPayloadFragment()
            val b = Bundle()
            b.putInt(ARG_TYPE, type)
            fragment.arguments = b
            return fragment
        }
    }
}