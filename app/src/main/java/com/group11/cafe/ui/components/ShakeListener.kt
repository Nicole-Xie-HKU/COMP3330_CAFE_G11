package com.group11.cafe.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.group11.cafe.utils.ShakeDetector

@Composable
fun ShakeListener(
    onShake: () -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val detector = ShakeDetector(onShake)

        if (accelerometer != null) {
            sensorManager.registerListener(
                detector,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        onDispose {
            sensorManager.unregisterListener(detector)
        }
    }
}
