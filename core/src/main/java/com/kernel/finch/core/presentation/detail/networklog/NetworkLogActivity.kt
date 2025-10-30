package com.kernel.finch.core.presentation.detail.networklog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.kernel.finch.FinchCore
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.core.R
import com.kernel.finch.core.data.db.FinchDatabase
import com.kernel.finch.core.presentation.detail.networklog.NetworkLogPayloadFragment.Companion.TYPE_REQUEST
import com.kernel.finch.core.presentation.detail.networklog.NetworkLogPayloadFragment.Companion.TYPE_RESPONSE
import com.kernel.finch.core.util.FileUtil
import com.kernel.finch.core.util.Text
import com.kernel.finch.core.util.extension.getUriForFile
import com.kernel.finch.core.util.formatFileName
import com.kernel.finch.utils.consume
import java.io.File
import java.util.*

internal class NetworkLogActivity : BaseFinchActivity() {

    private lateinit var adapter: Adapter

    private var networkLog: NetworkLogEntity? = null
    private var networkLogId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        FinchCore.implementation.configuration.themeResourceId?.let { setTheme(it) }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finch_activity_network_log)
        findViewById<Toolbar>(R.id.finch_toolbar).apply {
            try {
                setSupportActionBar(this)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } catch (e: IllegalStateException) {
                visibility = View.GONE
            }
        }

        findViewById<ViewPager>(R.id.finch_view_pager).let {
            setupViewPager(it)
            findViewById<TabLayout>(R.id.finch_tabs).setupWithViewPager(it)
        }

        networkLogId = intent.getLongExtra(ARG_NETWORK_LOG_ID, 0)

        FinchDatabase.getInstance(application)
            .networkLog()
            .getById(networkLogId)
            .observe(this, { value ->
                networkLog = value
                update()
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.finch_network_log, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.finch_share_text -> consume {
                networkLog?.apply {
                    share(Text.getShareText(this@NetworkLogActivity, this))
                }
            }

            R.id.finch_share_file -> consume {
                networkLog?.apply {
                    val uniqueFileName = "${formatFileName(requestDate)}-$id"
                    shareFile(
                        FileUtil.createTextFile(
                            this@NetworkLogActivity,
                            uniqueFileName,
                            Text.getShareText(this@NetworkLogActivity, this)
                        )
                    )
                }
            }

            R.id.finch_share_json -> consume {
                networkLog?.apply {
                    val uniqueFileName = "${formatFileName(requestDate)}-$id"
                    shareFile(
                        FileUtil.createJsonFile(
                            this@NetworkLogActivity,
                            uniqueFileName,
                            responseBody
                        )
                    )
                }
            }

            R.id.finch_share_curl -> consume {
                networkLog?.apply {
                    share(Text.getShareCurlCommand(this))
                }
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun update() {
        networkLog?.apply {
            findViewById<TextView>(R.id.finch_text_view)?.text = "$method $path"
            for (fragment in adapter.fragments) {
                fragment.networkLogUpdated(this)
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = Adapter(supportFragmentManager)
        adapter.addFragment(NetworkLogOverviewFragment(), getString(R.string.finch_overview))
        adapter.addFragment(
            NetworkLogPayloadFragment.newInstance(TYPE_REQUEST),
            getString(R.string.finch_request)
        )
        adapter.addFragment(
            NetworkLogPayloadFragment.newInstance(TYPE_RESPONSE),
            getString(R.string.finch_response)
        )
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

    private fun shareFile(file: File?) {
        file?.apply {
            val path = getUriForFile(file)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.setDataAndType(path, contentResolver.getType(path))
            sharingIntent.putExtra(Intent.EXTRA_STREAM, path)
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(sharingIntent, null))
        }
    }


    internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        val fragments: MutableList<NetworkLogFragment> = ArrayList()
        private val fragmentTitles = ArrayList<String>()

        fun addFragment(fragment: NetworkLogFragment, title: String) {
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

        private const val ARG_NETWORK_LOG_ID = "network_log_id"

        private var selectedTabPosition = 0

        fun start(context: Context, networkLogId: Long) {
            val intent = Intent(context, NetworkLogActivity::class.java)
            intent.putExtra(ARG_NETWORK_LOG_ID, networkLogId)
            context.startActivity(intent)
        }
    }

    abstract class SimpleOnPageChangedListener : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        abstract override fun onPageSelected(position: Int)
        override fun onPageScrollStateChanged(state: Int) {}
    }
}
