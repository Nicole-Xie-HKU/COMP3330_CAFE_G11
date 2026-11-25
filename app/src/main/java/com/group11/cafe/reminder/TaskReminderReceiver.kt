package com.group11.cafe.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.group11.cafe.R

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("taskId").orEmpty()
        val title = intent.getStringExtra("title").orEmpty()
        val eventName = intent.getStringExtra("eventName").orEmpty()
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(
                "tasks",
                "Task reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        val notification = NotificationCompat.Builder(context, "tasks")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task soon: $title")
            .setContentText(eventName)
            .setAutoCancel(true)
            .build()

        manager.notify(taskId.hashCode(), notification)
    }
}
