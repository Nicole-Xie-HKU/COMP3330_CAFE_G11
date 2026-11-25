package com.group11.cafe.utils

object EventDeleteSteps {

    val names = listOf(
        "sessions",
        "tasks",
        "announcements",
        "helpRequests",
        "attendance",
        "signups",
        "event"
    )

    fun run(
        steps: List<(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) -> Unit>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fun next(index: Int) {
            if (index == steps.size) {
                onSuccess()
                return
            }

            steps[index](
                { next(index + 1) },
                onFailure
            )
        }

        next(0)
    }
}
