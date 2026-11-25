package com.group11.cafe.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ParticipantQrParserTest {

    @Test
    fun parse_recognizesRegisteredParticipant() {
        val result = ParticipantQrParser.parse("CAFE-user123")

        assertEquals("user123", result?.id)
        assertFalse(result?.isGuest ?: true)
    }

    @Test
    fun parse_recognizesGuestParticipant() {
        val result = ParticipantQrParser.parse("CAFE-GUEST")

        assertEquals("CAFE-GUEST", result?.id)
        assertTrue(result?.isGuest ?: false)
    }

    @Test
    fun parse_rejectsOtherQrCodes() {
        assertNull(ParticipantQrParser.parse("https://hku.hk"))
        assertNull(ParticipantQrParser.parse("event1|session1|user1|Amy"))
        assertNull(ParticipantQrParser.parse("CAFE-"))
        assertNull(ParticipantQrParser.parse("CAFE-a/b"))
    }
}
