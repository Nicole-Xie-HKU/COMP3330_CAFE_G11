package com.group11.cafe.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AppBackButton(
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text("< Back")
    }
}
