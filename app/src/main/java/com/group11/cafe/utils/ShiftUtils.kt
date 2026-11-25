package com.group11.cafe.utils

import com.group11.cafe.data.model.Session

object ShiftUtils {

    fun sessionsToMove(
        sessions: List<Session>,
        startingSession: Session
    ): List<Session> {
        return sessions.filter {
            !it.fixedTime &&
                TimeUtils.isAtOrAfter(it.startTime, startingSession.startTime)
        }
    }

    fun move(
        sessions: List<Session>,
        minutes: Int
    ): List<Session> {
        return sessions.map {
            it.copy(
                startTime = TimeUtils.addMinutes(it.startTime, minutes.toLong()),
                endTime = TimeUtils.addMinutes(it.endTime, minutes.toLong()),
                movedMinutes = it.movedMinutes + minutes
            )
        }
    }

    fun passesMidnight(
        sessions: List<Session>,
        minutes: Int
    ): Boolean {
        return sessions.any {
            val start = TimeUtils.toMinutes(it.startTime) ?: 0
            val end = TimeUtils.toMinutes(it.endTime) ?: 0

            start + minutes < 0 || end + minutes >= 24 * 60
        }
    }

    fun fixedSessionsStaying(
        sessions: List<Session>,
        startingSession: Session
    ): List<Session> {
        return sessions.filter {
            it.fixedTime &&
                TimeUtils.isAtOrAfter(it.startTime, startingSession.startTime)
        }
    }

    fun clashes(
        sessions: List<Session>,
        moved: List<Session>
    ): List<String> {
        val staying = sessions.filter { session ->
            moved.none { it.id == session.id }
        }

        return staying
            .filter { session -> moved.any { overlaps(session, it) } }
            .map { it.title }
    }

    private fun overlaps(a: Session, b: Session): Boolean {
        val aStart = TimeUtils.toMinutes(a.startTime) ?: 0
        val aEnd = TimeUtils.toMinutes(a.endTime) ?: 0
        val bStart = TimeUtils.toMinutes(b.startTime) ?: 0
        val bEnd = TimeUtils.toMinutes(b.endTime) ?: 0

        return aStart < bEnd && bStart < aEnd
    }
}
