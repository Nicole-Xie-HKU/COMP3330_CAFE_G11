package com.group11.cafe.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import com.group11.cafe.data.model.Session

object SessionIntents {

    fun openDirections(context: Context, session: Session) {
        val uri = Uri.parse("geo:0,0?q=" + Uri.encode(session.location))
        open(context, Intent(Intent.ACTION_VIEW, uri))
    }

    fun addToCalendar(context: Context, session: Session, date: String) {
        val intent = Intent(Intent.ACTION_INSERT)
        intent.data = CalendarContract.Events.CONTENT_URI
        intent.putExtra(CalendarContract.Events.TITLE, session.title)
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, session.location)

        val start = TimeUtils.toMillis(date, session.startTime)
        val end = TimeUtils.toMillis(date, session.endTime)
        if (start != null && end != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start)
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
        }

        open(context, intent)
    }

    fun share(context: Context, session: Session) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "${session.title}, ${session.startTime} - ${session.endTime} at ${session.location}"
        )
        open(context, Intent.createChooser(intent, "Share session"))
    }

    private fun open(context: Context, intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No app found.", Toast.LENGTH_SHORT).show()
        }
    }
}
