package uz.jasurbek.notes.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import uz.jasurbek.notes.R


class NotificationHelper(context: Context) : ContextWrapper(context) {
    private val channelID = "uz.jas.notes.notification.channel.id"
    private val channelName = "Notes reminder notifications"

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()
    }

    private lateinit var manager: NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel =
            NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        managerInstance().createNotificationChannel(channel)
    }


    fun managerInstance(): NotificationManager {
        if (!::manager.isInitialized) {
            manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager
    }

    fun getChannelNotification(title: String, message: String): NotificationCompat.Builder =
        NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_note)


}