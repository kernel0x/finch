package com.kernel.finch.core.ui

import android.os.Bundle
import android.view.View
import com.kernel.finch.R
import com.kernel.finch.core.data.models.TransactionHttpEntity
import kotlinx.android.synthetic.main.lib_finch_activity_main.*


class MainActivity : BaseFinchActivity(), TransactionListFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_finch_activity_main)
        setSupportActionBar(toolbar)
        toolbar.subtitle = getApplicationName()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.layoutContainer, TransactionListFragment.newInstance())
                    .commit()
        }
    }

    override fun onListFragmentInteraction(view: View, item: TransactionHttpEntity) {
        TransactionActivity.start(this, item.id)
    }

    private fun getApplicationName(): String {
        val applicationInfo = applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
    }

}
