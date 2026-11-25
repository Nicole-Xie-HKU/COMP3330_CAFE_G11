package com.group11.cafe.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.sqrt

class ShakeDetector(
    private val onShake: () -> Unit
) : SensorEventListener {

    private var lastShake = 0L

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val force = sqrt(x * x + y * y + z * z)
        val now = System.currentTimeMillis()

        if (force > 25 && now - lastShake > 2000) {
            lastShake = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
