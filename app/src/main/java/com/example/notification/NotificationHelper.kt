package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.model.NotificationCategory

object NotificationHelper {
    const val CHANNEL_BREAKING = "channel_breaking_alerts"
    const val CHANNEL_CONTENT = "channel_content_releases"
    const val CHANNEL_ENGAGEMENT = "channel_engagement"

    const val EXTRA_TARGET_URL = "extra_target_url"
    const val EXTRA_TARGET_TAB = "extra_target_tab"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val breakingChannel = NotificationChannel(
                CHANNEL_BREAKING,
                "Breaking News & Urgent Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time alerts for breaking news and viral updates"
                enableVibration(true)
                setShowBadge(true)
            }

            val contentChannel = NotificationChannel(
                CHANNEL_CONTENT,
                "New Releases & Videos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily featured content and recommended articles"
                enableVibration(true)
                setShowBadge(true)
            }

            val engagementChannel = NotificationChannel(
                CHANNEL_ENGAGEMENT,
                "Community & Digest Alerts",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Daily reading streak and engagement milestones"
                setShowBadge(false)
            }

            notificationManager.createNotificationChannels(
                listOf(breakingChannel, contentChannel, engagementChannel)
            )
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun sendSystemNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        category: NotificationCategory = NotificationCategory.BREAKING,
        targetUrl: String = "https://noodlemagazine.com/home",
        targetTab: String = "portal"
    ) {
        if (!hasNotificationPermission(context)) {
            return
        }

        val channelId = when (category) {
            NotificationCategory.BREAKING -> CHANNEL_BREAKING
            NotificationCategory.NEW_RELEASE -> CHANNEL_CONTENT
            NotificationCategory.ENGAGEMENT -> CHANNEL_ENGAGEMENT
            NotificationCategory.RECOMMENDED -> CHANNEL_CONTENT
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TARGET_URL, targetUrl)
            putExtra(EXTRA_TARGET_TAB, targetTab)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(
                if (category == NotificationCategory.BREAKING) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Handled safely
        }
    }
}
