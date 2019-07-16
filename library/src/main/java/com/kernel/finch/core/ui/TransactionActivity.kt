package com.kernel.finch.core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.kernel.finch.R
import com.kernel.finch.core.data.FinchDatabase
import com.kernel.finch.core.data.models.TransactionHttpEntity
import com.kernel.finch.core.ui.TransactionPayloadFragment.Companion.TYPE_REQUEST
import com.kernel.finch.core.ui.TransactionPayloadFragment.Companion.TYPE_RESPONSE
import com.kernel.finch.core.utils.FormatUtil
import kotlinx.android.synthetic.main.lib_finch_activity_transaction.*
import java.util.*

class TransactionActivity : BaseFinchActivity() {

    private lateinit var adapter: Adapter

    private var transactionHttp: TransactionHttpEntity? = null
    private var transactionHttpId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_finch_activity_transaction)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewPager(viewPager)
        layoutTabs.setupWithViewPager(viewPager)

        transactionHttpId = intent.getLongExtra(ARG_TRANSACTION_HTTP_ID, 0)
        FinchDatabase.getInstance(application).transactionHttp().getById(transactionHttpId).observe(this, Observer { value ->
            transactionHttp = value
            update()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.lib_finch_transaction, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.share_text -> {
                share(FormatUtil.getShareText(this, transactionHttp!!))
                true
            }
            item.itemId == R.id.share_curl -> {
                share(FormatUtil.getShareCurlCommand(transactionHttp!!))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun update() {
        if (transactionHttp != null) {
            viewToolbarTitle.text = transactionHttp!!.method + " " + transactionHttp!!.path
            for (fragment in adapter.fragments) {
                fragment.transactionUpdated(transactionHttp!!)
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = Adapter(supportFragmentManager)
        adapter.addFragment(TransactionOverviewFragment(), getString(R.string.lib_finch_overview))
        adapter.addFragment(TransactionPayloadFragment.newInstance(TYPE_REQUEST), getString(R.string.lib_finch_request))
        adapter.addFragment(TransactionPayloadFragment.newInstance(TYPE_RESPONSE), getString(R.string.lib_finch_response))
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : SimpleOnPageChangedListener() {
            override fun onPageSelected(position: Int) {
                selectedTabPosition = position
            }
        })
        viewPager.currentItem = selectedTabPosition
    }

    private fun share(content: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, null))
    }

    internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        val fragments: MutableList<TransactionFragment> = ArrayList()
        private val fragmentTitles = ArrayList<String>()

        fun addFragment(fragment: TransactionFragment, title: String) {
            fragments.add(fragment)
            fragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position] as Fragment
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitles[position]
        }
    }

    companion object {
        private const val ARG_TRANSACTION_HTTP_ID = "transaction_http_id"

        private var selectedTabPosition = 0

        fun start(context: Context, transactionHttpId: Long) {
            val intent = Intent(context, TransactionActivity::class.java)
            intent.putExtra(ARG_TRANSACTION_HTTP_ID, transactionHttpId)
            context.startActivity(intent)
        }
    }

    abstract class SimpleOnPageChangedListener : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        abstract override fun onPageSelected(position: Int)
        override fun onPageScrollStateChanged(state: Int) {}
    }
}
