package com.kernel.finch.core.presentation.detail.networklog

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.kernel.finch.FinchCore
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.core.R

internal class ContainerActivity : BaseFinchActivity(),
    NetworkLogListFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        FinchCore.implementation.configuration.themeResourceId?.let { setTheme(it) }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finch_activity_container)
        findViewById<Toolbar>(R.id.finch_toolbar).apply {
            try {
                setSupportActionBar(this)
                subtitle = getApplicationName()
            } catch (e: IllegalStateException) {
                visibility = View.GONE
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.finch_container, NetworkLogListFragment.newInstance())
                .commit()
        }
    }

    override fun onListFragmentInteraction(view: View, item: NetworkLogEntity) {
        NetworkLogActivity.start(this, item.id)
    }

    private fun getApplicationName(): String {
        val applicationInfo = applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(
            stringId
        )
    }
}
