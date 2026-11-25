package com.group11.cafe.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignupButtonTest {

    @Test
    fun visible_hidesGuests() {
        assertFalse(SignupButton.visible(true))
        assertTrue(SignupButton.visible(false))
    }

    @Test
    fun label_followsWhetherTheyAreGoing() {
        assertEquals("I'm going", SignupButton.label(false))
        assertEquals("You're going", SignupButton.label(true))
    }

    @Test
    fun newSignup_fillsABlankName() {
        val signup = SignupButton.newSignup(
            "e1",
            "Judging",
            "2026-11-30",
            "u1",
            "",
            10
        )

        assertEquals("e1_u1", signup.id)
        assertEquals("e1", signup.eventId)
        assertEquals("Judging", signup.eventName)
        assertEquals("2026-11-30", signup.eventDate)
        assertEquals("u1", signup.userId)
        assertEquals("Participant", signup.participantName)
        assertEquals(10L, signup.createdAt)
    }

    @Test
    fun newSignup_keepsAda() {
        val signup = SignupButton.newSignup(
            "e1",
            "Judging",
            "2026-11-30",
            "u1",
            "Ada",
            10
        )

        assertEquals("Ada", signup.participantName)
    }

    @Test
    fun canPress_waitsForARealEventAndAnIdleWrite() {
        assertFalse(SignupButton.canPress(true, "e1"))
        assertFalse(SignupButton.canPress(false, ""))
        assertTrue(SignupButton.canPress(false, "e1"))
    }
}
