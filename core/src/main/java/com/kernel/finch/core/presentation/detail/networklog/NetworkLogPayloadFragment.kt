package com.kernel.finch.core.presentation.detail.networklog

import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.kernel.finch.common.loggers.data.models.MediaType
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.core.R
import com.kernel.finch.utils.extensions.argument

internal class NetworkLogPayloadFragment : Fragment(), NetworkLogFragment,
    SearchView.OnQueryTextListener {

    private var type: Int by argument()
    private var networkLog: NetworkLogEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        LayoutInflater.from(context)
            .inflate(R.layout.finch_fragment_network_log_payload, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
    }

    override fun networkLogUpdated(networkLog: NetworkLogEntity) {
        this.networkLog = networkLog
        update()
    }

    private fun update() {
        if (isAdded) {
            networkLog?.run {
                when (type) {
                    TYPE_REQUEST -> setText(
                        getRequestHeadersString(true),
                        getFormattedRequestBody(),
                        requestBodyIsPlainText
                    )

                    TYPE_RESPONSE -> setText(
                        getResponseHeadersString(true),
                        getFormattedResponseBody(),
                        responseBodyIsPlainText
                    )
                }
            }
        }
    }

    private fun setText(headersString: String, bodyString: String, isPlainText: Boolean) {
        view?.run {
            findViewById<TextView>(R.id.finch_headers).run {
                visibility = if (TextUtils.isEmpty(headersString)) View.GONE else View.VISIBLE
                text = Html.fromHtml(headersString)
            }
            findViewById<TextView>(R.id.finch_body).run {
                text = if (!isPlainText) {
                    getString(R.string.finch_body_omitted)
                } else {
                    bodyString
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.finch_search)
        searchMenuItem.isVisible = true
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(true)

        menu.findItem(R.id.finch_share_json)?.isVisible =
            networkLog?.responseContentType == MediaType.APPLICATION_JSON

        super.onPrepareOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        view?.run {
            findViewById<TextView>(R.id.finch_body)?.run {
                var fullText = text.toString()
                if (newText.isEmpty() || !fullText.contains(newText) || TextUtils.isEmpty(newText)) {
                    update()
                } else {
                    val indexOfCriteria = fullText.indexOf(newText)
                    val lineNumber = layout.getLineForOffset(indexOfCriteria)
                    val highlighted = "<font color='red'>$newText</font>"
                    fullText = fullText.replace(newText, highlighted)
                    text = Html.fromHtml(fullText)
                    findViewById<ScrollView>(R.id.finch_scroll_view)?.scrollTo(
                        0,
                        layout.getLineTop(lineNumber)
                    )
                }
            }
        }
        return true
    }

    companion object {

        const val TYPE_REQUEST = 0
        const val TYPE_RESPONSE = 1

        fun newInstance(type: Int) =
            NetworkLogPayloadFragment().apply {
                this.type = type
            }
    }
}
