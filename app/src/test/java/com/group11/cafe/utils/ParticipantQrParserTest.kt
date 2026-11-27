
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

    @Test
    fun parse_rejectsEmptyInput() {
        assertNull(ParticipantQrParser.parse(""))
    }

    @Test
    fun parse_rejectsWhitespaceOnlyInput() {
        assertNull(ParticipantQrParser.parse("   "))
        assertNull(ParticipantQrParser.parse("\n\t"))
    }

    @Test
    fun parse_trimsParticipantQrWhitespace() {
        val result = ParticipantQrParser.parse("  CAFE-user123  ")

        assertEquals("user123", result?.id)
        assertFalse(result?.isGuest ?: true)
    }

    @Test
    fun parse_trimsGuestQrWhitespace() {
        val result = ParticipantQrParser.parse("  CAFE-GUEST  ")

        assertEquals("CAFE-GUEST", result?.id)
        assertTrue(result?.isGuest ?: false)
    }

    @Test
    fun parse_rejectsIncorrectPrefixCasing() {
        assertNull(ParticipantQrParser.parse("cafe-user123"))
        assertNull(ParticipantQrParser.parse("Cafe-user123"))
    }

    @Test
    fun parse_rejectsParticipantIdContainingMultipleSlashes() {
        assertNull(ParticipantQrParser.parse("CAFE-user/123/test"))
    }

    @Test
    fun parse_rejectsPrefixFollowedOnlyByWhitespace() {
        assertNull(ParticipantQrParser.parse("CAFE-   "))
    }

    @Test
    fun parse_preservesHyphensInsideParticipantId() {
        val result = ParticipantQrParser.parse("CAFE-user-123-test")

        assertEquals("user-123-test", result?.id)
        assertFalse(result?.isGuest ?: true)
    }

    @Test
    fun parse_distinguishesGuestFromRegisteredParticipant() {
        val guest = ParticipantQrParser.parse("CAFE-GUEST")
        val participant = ParticipantQrParser.parse("CAFE-guest")

        assertTrue(guest?.isGuest ?: false)
        assertFalse(participant?.isGuest ?: true)
        assertEquals("guest", participant?.id)
    }
}
