package uz.jasurbek.notes.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import uz.jasurbek.notes.util.NotificationHelper


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent:  Intent?) {
        val notificationHelper = NotificationHelper(context)
        val notificationBuilder = notificationHelper.getChannelNotification()
        notificationHelper.managerInstance().notify(1, notificationBuilder.build())
    }

}