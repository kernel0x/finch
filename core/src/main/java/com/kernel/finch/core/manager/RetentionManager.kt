package com.kernel.finch.core.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.kernel.finch.core.data.db.FinchDatabase
import com.kernel.finch.core.data.models.Period
import java.util.concurrent.TimeUnit

internal class RetentionManager(private val context: Context, retentionPeriod: Period) {
    private val period: Long
    private val cleanupFrequency: Long
    private val prefs: SharedPreferences

    init {
        period = toMillis(retentionPeriod)
        prefs = context.getSharedPreferences(PREFS_NAME, 0)
        cleanupFrequency = if (retentionPeriod == Period.ONE_HOUR) {
            TimeUnit.MINUTES.toMillis(30)
        } else {
            TimeUnit.HOURS.toMillis(2)
        }
    }

    @Synchronized
    fun processing() {
        if (period > 0) {
            val now = System.currentTimeMillis()
            if (isCleanupDue(now)) {
                Log.i(TAG, "Performing data retention maintenance...")
                deleteSince(getThreshold(now))
                updateLastCleanup(now)
            }
        }
    }

    private fun getLastCleanup(fallback: Long): Long {
        if (lastCleanup == 0L) {
            lastCleanup = prefs.getLong(KEY_LAST_CLEANUP, fallback)
        }
        return lastCleanup
    }

    private fun updateLastCleanup(time: Long) {
        lastCleanup = time
        prefs.edit().putLong(KEY_LAST_CLEANUP, time).apply()
    }

    private fun deleteSince(threshold: Long) {
        FinchDatabase.getInstance(context).networkLog().deleteWhereRequestDate(threshold)
    }

    private fun isCleanupDue(now: Long): Boolean {
        return now - getLastCleanup(now) > cleanupFrequency
    }

    private fun getThreshold(now: Long): Long {
        return if (period == 0L) now else now - period
    }

    private fun toMillis(period: Period): Long {
        return when (period) {
            Period.ONE_HOUR -> TimeUnit.HOURS.toMillis(1)
            Period.ONE_DAY -> TimeUnit.DAYS.toMillis(1)
            Period.ONE_WEEK -> TimeUnit.DAYS.toMillis(7)
            else -> 0
        }
    }

    companion object {
        private const val TAG = "Finch"
        private const val PREFS_NAME = "finch_preferences"
        private const val KEY_LAST_CLEANUP = "last_cleanup"

        private var lastCleanup: Long = 0
    }
}
