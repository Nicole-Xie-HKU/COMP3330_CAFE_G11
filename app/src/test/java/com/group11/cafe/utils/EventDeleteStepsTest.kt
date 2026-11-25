package com.group11.cafe.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class EventDeleteStepsTest {

    @Test
    fun names_areTheDeleteOrder() {
        assertEquals(
            listOf(
                "sessions",
                "tasks",
                "announcements",
                "helpRequests",
                "attendance",
                "signups",
                "event"
            ),
            EventDeleteSteps.names
        )
    }

    @Test
    fun run_callsThreeStepsInOrder() {
        val ran = mutableListOf<String>()

        EventDeleteSteps.run(
            steps = listOf(
                { onSuccess, _ ->
                    ran.add("sessions")
                    onSuccess()
                },
                { onSuccess, _ ->
                    ran.add("tasks")
                    onSuccess()
                },
                { onSuccess, _ ->
                    ran.add("announcements")
                    onSuccess()
                }
            ),
            onSuccess = { ran.add("done") },
            onFailure = { ran.add("failed") }
        )

        assertEquals(listOf("sessions", "tasks", "announcements", "done"), ran)
    }

    @Test
    fun run_stopsWhenTheSecondStepFails() {
        val ran = mutableListOf<String>()
        var message: String? = null

        EventDeleteSteps.run(
            steps = listOf(
                { onSuccess, _ ->
                    ran.add("sessions")
                    onSuccess()
                },
                { _, onFailure ->
                    ran.add("tasks")
                    onFailure(Exception("Could not delete tasks."))
                },
                { _, _ ->
                    ran.add("announcements")
                }
            ),
            onSuccess = { ran.add("done") },
            onFailure = { message = it.message }
        )

        assertEquals(listOf("sessions", "tasks"), ran)
        assertEquals("Could not delete tasks.", message)
    }
}
