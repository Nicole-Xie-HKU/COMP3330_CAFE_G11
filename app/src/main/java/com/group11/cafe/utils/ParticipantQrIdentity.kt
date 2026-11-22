package com.group11.cafe.utils

data class ParticipantQrIdentity(
    val id: String,
    val isGuest: Boolean
)

object ParticipantQrParser {

    private const val PARTICIPANT_PREFIX = "CAFE-"
    const val GUEST_QR_CODE = "CAFE-GUEST"

    fun parse(value: String): ParticipantQrIdentity {
        val trimmedValue = value.trim()

        if (trimmedValue == GUEST_QR_CODE) {
            return ParticipantQrIdentity(
                id = GUEST_QR_CODE,
                isGuest = true
            )
        }

        return ParticipantQrIdentity(
            id = trimmedValue.removePrefix(PARTICIPANT_PREFIX),
            isGuest = false
        )
    }
}
