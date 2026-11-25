package com.group11.cafe.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group11.cafe.data.model.Session
import com.group11.cafe.ui.components.NowDot
import com.group11.cafe.ui.components.currentTime
import com.group11.cafe.utils.TimeUtils
import androidx.compose.foundation.clickable

@Composable
fun SessionListScreen(
    sessions: List<Session>,
    eventDate: String,
    onSessionClick: (Session) -> Unit,
    modifier: Modifier = Modifier
) {

    val now = currentTime()
    val sortedSessions = sessions.sortedBy { TimeUtils.toMinutes(it.startTime) }
    val nextSession = sortedSessions.find {
        val start = TimeUtils.toMillis(eventDate, it.startTime)
        start != null && start > now
    }

    LazyColumn(modifier = modifier) {

        items(sortedSessions) { session ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onSessionClick(session)
                    }
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = session.title,
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Text(session.location)

                    Text(
                        "${session.startTime} - ${session.endTime}"
                    )

                    if (TimeUtils.isNow(eventDate, session.startTime, session.endTime, now)) {
                        NowDot()
                    } else if (session.id == nextSession?.id) {
                        Text("Next")
                    }

                    if (session.movedMinutes > 0) {
                        Text(
                            text = "Moved ${session.movedMinutes} min later",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (session.movedMinutes < 0) {
                        Text(
                            text = "Moved ${-session.movedMinutes} min earlier",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}