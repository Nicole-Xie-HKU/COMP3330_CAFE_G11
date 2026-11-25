package com.group11.cafe.utils

import android.content.Context
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import com.group11.cafe.R

object Feedback {

    fun success(context: Context) {
        playSound(context, R.raw.beep_ok)
        vibrate(context, 150)
    }

    fun error(context: Context) {
        playSound(context, R.raw.beep_error)
        vibrate(context, 500)
    }

    private fun playSound(context: Context, sound: Int) {
        val mediaPlayer = MediaPlayer.create(context, sound)
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
        mediaPlayer?.start()
    }

    private fun vibrate(context: Context, milliseconds: Long) {
        val vibrator = context.getSystemService(Vibrator::class.java)
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                milliseconds,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }
}
