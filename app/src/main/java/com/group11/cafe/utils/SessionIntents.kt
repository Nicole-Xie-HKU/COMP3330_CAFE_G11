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
        val location = session.location.trim()

        if (location.isBlank()) {
            Toast.makeText(
                context,
                "Venue location is unavailable.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(location))
        val geoIntent = Intent(Intent.ACTION_VIEW, geoUri)

        try {
            context.startActivity(geoIntent)
        } catch (e: ActivityNotFoundException) {
            // Fall back to Google Maps in a browser.
            val mapsUri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(location)}"
            )

            try {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, mapsUri)
                )
            } catch (fallbackException: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "Unable to open directions.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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
