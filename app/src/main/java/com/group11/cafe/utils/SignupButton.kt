package com.group11.cafe.utils

import com.group11.cafe.data.model.Signup

object SignupButton {

    fun label(signedUp: Boolean): String {
        return if (signedUp) {
            "You're going"
        } else {
            "I'm going"
        }
    }

    fun visible(isGuest: Boolean): Boolean {
        return !isGuest
    }

    fun newSignup(
        eventId: String,
        eventName: String,
        eventDate: String,
        userId: String,
        participantName: String,
        nowMillis: Long
    ): Signup {
        val name = if (participantName.isBlank()) {
            "Participant"
        } else {
            participantName
        }

        return Signup(
            id = "${eventId}_$userId",
            eventId = eventId,
            eventName = eventName,
            eventDate = eventDate,
            userId = userId,
            participantName = name,
            createdAt = nowMillis
        )
    }

    fun canPress(isSaving: Boolean, eventId: String): Boolean {
        return !isSaving && eventId.isNotBlank()
    }
}
