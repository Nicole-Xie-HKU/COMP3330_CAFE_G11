package com.group11.cafe.ui.components

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun currentTime(): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val ticker = object : Runnable {
            override fun run() {
                now = System.currentTimeMillis()
                handler.postDelayed(this, 30000)
            }
        }
        handler.post(ticker)

        onDispose {
            handler.removeCallbacks(ticker)
        }
    }

    return now
}
