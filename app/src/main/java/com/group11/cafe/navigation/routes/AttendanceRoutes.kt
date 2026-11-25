package com.group11.cafe.navigation.routes

import androidx.compose.runtime.Composable
import com.group11.cafe.ui.attendance.AttendanceListPage
import com.group11.cafe.ui.attendance.AttendanceScannerPage

@Composable
fun AttendanceScannerRoute(eventId: String?, sessionId: String?) {
    AttendanceScannerPage(
        eventId = eventId.orEmpty(),
        sessionId = sessionId.orEmpty()
    )
}

@Composable
fun AttendanceListRoute(eventId: String?, sessionId: String?) {
    AttendanceListPage(
        eventId = eventId.orEmpty(),
        sessionId = sessionId.orEmpty()
    )
}
