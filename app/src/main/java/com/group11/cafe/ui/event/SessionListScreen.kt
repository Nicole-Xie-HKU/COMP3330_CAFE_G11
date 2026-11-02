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
import androidx.compose.foundation.clickable

@Composable
fun SessionListScreen(
    sessions: List<Session>,
    onSessionClick: (Session) -> Unit
) {

    LazyColumn {

        items(sessions) { session ->

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
                }
            }
        }
    }
}