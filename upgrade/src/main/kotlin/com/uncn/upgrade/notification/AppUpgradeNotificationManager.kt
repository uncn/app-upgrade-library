package com.uncn.upgrade.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.uncn.upgrade.AppUpgradeLogger
import com.uncn.upgrade.receiver.AppInstallReceiver

class AppUpgradeNotificationManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "app_upgrade_channel"
        private const val NOTIFICATION_ID_DOWNLOAD = 1001
        private const val NOTIFICATION_ID_INSTALL = 1002
        private const val CHANNEL_NAME = "APP Upgrade"
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for APP upgrades"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showDownloadingNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Downloading Update")
            .setContentText("Downloading in progress...")
            .setSmallIcon(android.R.drawable.ic_menu_download)
            .setProgress(100, 0, false)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_DOWNLOAD, notification)
        AppUpgradeLogger.d("Showing downloading notification")
    }

    fun updateProgress(progress: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Downloading Update")
            .setContentText("$progress% downloaded")
            .setSmallIcon(android.R.drawable.ic_menu_download)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_DOWNLOAD, notification)
    }

    fun showInstallNotification(apkPath: String) {
        val intent = Intent(context, AppInstallReceiver::class.java).apply {
            action = AppInstallReceiver.ACTION_INSTALL
            putExtra(AppInstallReceiver.EXTRA_APK_PATH, apkPath)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Update Complete")
            .setContentText("Tap to install the update")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_info_details, "Install", pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID_INSTALL, notification)
        AppUpgradeLogger.d("Showing install notification")
    }

    fun showErrorNotification(errorMsg: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Download Failed")
            .setContentText(errorMsg)
            .setSmallIcon(android.R.drawable.ic_menu_close_clear_cancel)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_DOWNLOAD, notification)
        AppUpgradeLogger.e("Showing error notification: $errorMsg")
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID_DOWNLOAD)
        notificationManager.cancel(NOTIFICATION_ID_INSTALL)
        AppUpgradeLogger.d("Notifications cancelled")
    }

    fun release() {
        cancelNotification()
    }
}