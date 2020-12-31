package com.kernel.finch.core.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.LongSparseArray
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.core.R
import com.kernel.finch.core.presentation.detail.networklog.BaseFinchActivity
import com.kernel.finch.core.presentation.detail.networklog.ClearNetworkLogService
import com.kernel.finch.core.util.FinchUtil

internal class NotificationManager(private val context: Context) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.finch_network_logger_notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    @Synchronized
    fun show(networkLog: NetworkLogEntity) {
        addToBuffer(networkLog)
        if (!BaseFinchActivity.isInForeground) {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        FinchUtil.getLaunchIntent(context),
                        0
                    )
                )
                .setLocalOnly(true)
                .setSmallIcon(R.drawable.finch_ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.finch_notification))
                .setContentTitle(context.getString(R.string.finch_notification_title))

            val inboxStyle = NotificationCompat.InboxStyle()
            for ((count, i) in (networkLogBuffer.size() - 1 downTo 0).withIndex()) {
                if (count < BUFFER_SIZE) {
                    if (count == 0) {
                        builder.setContentText(networkLogBuffer.valueAt(i).getNotificationText())
                    }
                    inboxStyle.addLine(networkLogBuffer.valueAt(i).getNotificationText())
                }
            }
            builder.setAutoCancel(true)
            builder.setStyle(inboxStyle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setSubText(networkLogCount.toString())
            } else {
                builder.setNumber(networkLogCount)
            }
            builder.addAction(clearAction)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private val clearAction: NotificationCompat.Action
        get() {
            val clearTitle = context.getString(R.string.finch_clear)
            val deleteIntent = Intent(context, ClearNetworkLogService::class.java)
            val intent =
                PendingIntent.getService(context, 11, deleteIntent, PendingIntent.FLAG_ONE_SHOT)
            return NotificationCompat.Action(R.drawable.finch_ic_delete, clearTitle, intent)
        }

    fun dismiss() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val CHANNEL_ID = "channel_finch_network_logger"
        private const val NOTIFICATION_ID = 100902
        private const val BUFFER_SIZE = 10

        private val networkLogBuffer = LongSparseArray<NetworkLogEntity>()
        private var networkLogCount: Int = 0

        @Synchronized
        fun clearBuffer() {
            networkLogBuffer.clear()
            networkLogCount = 0
        }

        @Synchronized
        private fun addToBuffer(networkLog: NetworkLogEntity) {
            if (networkLog.getStatus() === NetworkLogEntity.Status.PROGRESS) {
                networkLogCount++
            }
            networkLogBuffer.put(networkLog.id, networkLog)
            if (networkLogBuffer.size() > BUFFER_SIZE) {
                networkLogBuffer.removeAt(0)
            }
        }
    }
}
