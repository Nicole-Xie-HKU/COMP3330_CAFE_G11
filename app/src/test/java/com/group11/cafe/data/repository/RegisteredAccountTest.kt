package com.group11.cafe.data.repository

import com.group11.cafe.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RegisteredAccountTest {

    @Test
    fun nullUid_failsAndBuildsNoUser() {
        val account = registeredAccount(
            uid = null,
            name = "Amy",
            email = "amy@example.com",
            role = UserRole.PARTICIPANT,
            inviteCode = "ABC123"
        )

        assertNull(account.user)
        assertEquals("Could not create the account.", account.error?.message)
    }

    @Test
    fun organizer_keepsInviteCodeAndNoQr() {
        val account = registeredAccount(
            uid = "user-1",
            name = "Amy",
            email = "amy@example.com",
            role = UserRole.ORGANIZER,
            inviteCode = "ABC123"
        )

        val user = account.user
        assertNull(account.error)
        assertEquals("user-1", user?.id)
        assertEquals("Organizer", user?.role)
        assertEquals(UserRole.ORGANIZER, user?.role)
        assertEquals("ABC123", user?.inviteCode)
        assertEquals("", user?.participantQrCode)
    }

    @Test
    fun participant_keepsQrAndNoInviteCode() {
        val account = registeredAccount(
            uid = "user-2",
            name = "Bo",
            email = "bo@example.com",
            role = UserRole.PARTICIPANT,
            inviteCode = "ABC123"
        )

        val user = account.user
        assertNull(account.error)
        assertEquals("Participant", user?.role)
        assertEquals(UserRole.PARTICIPANT, user?.role)
        assertEquals("", user?.inviteCode)
        assertEquals("CAFE-user-2", user?.participantQrCode)
    }

    @Test
    fun crew_getsNeitherInviteCodeNorQr() {
        val account = registeredAccount(
            uid = "user-3",
            name = "Cam",
            email = "cam@example.com",
            role = UserRole.CREW,
            inviteCode = "ABC123"
        )

        val user = account.user
        assertEquals("", user?.inviteCode)
        assertEquals("", user?.participantQrCode)
    }
}
