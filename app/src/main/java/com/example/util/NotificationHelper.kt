package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.BirthdayContact

object NotificationHelper {
  const val CHANNEL_ID = "celebrate_birthday_channel"
  private const val CHANNEL_NAME = "Birthday Celebrations"
  private const val CHANNEL_DESC = "Notifications and countdown alerts for upcoming birthdays"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = CHANNEL_DESC
        enableVibration(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  fun sendBirthdayReminderNotification(
    context: Context,
    contact: BirthdayContact,
    notificationId: Int = 1001
  ) {
    createNotificationChannel(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val days = contact.daysUntil()
    val title = if (days == 0) {
      "🎉 It's ${contact.name}'s Birthday Today!"
    } else if (days == 1) {
      "🎈 Tomorrow is ${contact.name}'s Birthday!"
    } else {
      "🎂 ${contact.name}'s Birthday is in $days days!"
    }

    val turningText = contact.turningAge()?.let { "Turning $it years old! " } ?: ""
    val body = "$turningText Don't forget to send warm wishes or check gift ideas 🎁"

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle(title)
      .setContentText(body)
      .setStyle(NotificationCompat.BigTextStyle().bigText("$body\n\nNotes: ${contact.notes.ifBlank { "Celebrate together!" }}"))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    try {
      notificationManager.notify(notificationId, builder.build())
    } catch (_: SecurityException) {
      // Permission not granted
    }
  }

  fun sendTestNotification(context: Context) {
    createNotificationChannel(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("🎉 Celebrate: Notifications are Ready!")
      .setContentText("You'll receive cheerful countdown reminders before your loved ones' birthdays 🎂")
      .setStyle(NotificationCompat.BigTextStyle().bigText("You'll receive cheerful countdown reminders before your loved ones' birthdays 🎂\nNever miss a celebration with family, friends, and colleagues!"))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    try {
      notificationManager.notify(9999, builder.build())
    } catch (_: SecurityException) {
      // Permission not granted
    }
  }
}
