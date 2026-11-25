package com.group11.cafe.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class EventTest {

    @Test
    fun newEvent_hasNoCreatedAt() {
        assertEquals(0L, Event().createdAt)
    }

    @Test
    fun copy_keepsCreatedAtAndOrganizer() {
        val loaded = Event(
            id = "event-1",
            name = "Old name",
            description = "Old description",
            date = "2026-11-01",
            organizerId = "organizer-1",
            createdAt = 1_700_000_000_000L
        )

        val saved = loaded.copy(
            name = "New name",
            description = "Updated",
            date = "2026-11-30"
        )

        assertEquals(loaded.createdAt, saved.createdAt)
        assertEquals(loaded.organizerId, saved.organizerId)
    }
}
