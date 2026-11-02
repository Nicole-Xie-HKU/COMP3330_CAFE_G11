package com.group11.cafe.ui.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group11.cafe.data.model.Event

@Composable
fun EventListScreen(
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {

    LazyColumn {

        items(events) { event ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onEventClick(event)
                    }
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = event.name,
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = event.description
                    )
                }
            }
        }
    }
}