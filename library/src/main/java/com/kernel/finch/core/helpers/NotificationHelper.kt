package com.kernel.finch.core.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.LongSparseArray
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kernel.finch.FinchUtil
import com.kernel.finch.R
import com.kernel.finch.core.data.models.TransactionHttpEntity
import com.kernel.finch.core.ui.BaseFinchActivity
import com.kernel.finch.core.ui.ClearTransactionsService

class NotificationHelper(private val context: Context) {
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.lib_finch_category_notification),
                    NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Synchronized
    fun show(transaction: TransactionHttpEntity) {
        addToBuffer(transaction)
        if (!BaseFinchActivity.isInForeground) {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentIntent(PendingIntent.getActivity(context, 0, FinchUtil.getLaunchIntent(context), 0))
                    .setLocalOnly(true)
                    .setSmallIcon(R.drawable.lib_finch_ic_notification)
                    .setColor(ContextCompat.getColor(context, R.color.lib_finch_notification))
                    .setContentTitle(context.getString(R.string.lib_finch_notification_title))

            val inboxStyle = NotificationCompat.InboxStyle()
            for ((count, i) in (transactionBuffer.size() - 1 downTo 0).withIndex()) {
                if (count < BUFFER_SIZE) {
                    if (count == 0) {
                        builder.setContentText(transactionBuffer.valueAt(i).getNotificationText())
                    }
                    inboxStyle.addLine(transactionBuffer.valueAt(i).getNotificationText())
                }
            }
            builder.setAutoCancel(true)
            builder.setStyle(inboxStyle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setSubText(transactionCount.toString())
            } else {
                builder.setNumber(transactionCount)
            }
            builder.addAction(clearAction)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private val clearAction: NotificationCompat.Action
        get() {
            val clearTitle = context.getString(R.string.lib_finch_clear)
            val deleteIntent = Intent(context, ClearTransactionsService::class.java)
            val intent = PendingIntent.getService(context, 11, deleteIntent, PendingIntent.FLAG_ONE_SHOT)
            return NotificationCompat.Action(R.drawable.lib_finch_ic_delete, clearTitle, intent)
        }

    fun dismiss() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val CHANNEL_ID = "finch"
        private const val NOTIFICATION_ID = 100902
        private const val BUFFER_SIZE = 10

        private val transactionBuffer = LongSparseArray<TransactionHttpEntity>()
        private var transactionCount: Int = 0

        @Synchronized
        fun clearBuffer() {
            transactionBuffer.clear()
            transactionCount = 0
        }

        @Synchronized
        private fun addToBuffer(transaction: TransactionHttpEntity) {
            if (transaction.getStatus() === TransactionHttpEntity.Status.PROGRESS) {
                transactionCount++
            }
            transactionBuffer.put(transaction.id, transaction)
            if (transactionBuffer.size() > BUFFER_SIZE) {
                transactionBuffer.removeAt(0)
            }
        }
    }
}
