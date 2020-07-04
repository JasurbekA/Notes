package uz.jasurbek.notes.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import uz.jasurbek.notes.data.Constants
import uz.jasurbek.notes.util.NotificationHelper


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val notificationHelper = NotificationHelper(context)
        val notificationTitle =
            intent?.getStringExtra(Constants.NOTIFICATION_BUNDLE_KEY_TITLE) ?: "Note app"
        val notificationBody =
            intent?.getStringExtra(Constants.NOTIFICATION_BUNDLE_KEY_BODY) ?: "Reminder to do task"
        val notificationBuilder =
            notificationHelper.getChannelNotification(notificationTitle, notificationBody)
        notificationHelper.managerInstance()
            .notify(Constants.NOTIFICATION_ID, notificationBuilder.build())
    }

}