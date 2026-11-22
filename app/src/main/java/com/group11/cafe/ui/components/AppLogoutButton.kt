package com.group11.cafe.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppLogoutButton(
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text("Log out")
    }
}
