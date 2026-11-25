package com.group11.cafe.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.group11.cafe.data.model.Task

object TaskReminder {

    private const val TEN_MINUTES = 10 * 60 * 1000L

    fun set(context: Context, task: Task, taskTime: Long) {
        val remindAt = taskTime - TEN_MINUTES

        if (task.done || remindAt < System.currentTimeMillis()) {
            alarmManager(context).cancel(pendingIntent(context, task))
            return
        }

        alarmManager(context).set(
            AlarmManager.RTC_WAKEUP,
            remindAt,
            pendingIntent(context, task)
        )
    }

    private fun alarmManager(context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun pendingIntent(context: Context, task: Task): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java)
        intent.putExtra("taskId", task.id)
        intent.putExtra("title", task.title)
        intent.putExtra("eventName", task.eventName)

        return PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
