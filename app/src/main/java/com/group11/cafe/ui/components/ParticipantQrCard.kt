package com.group11.cafe.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun ParticipantQrCard(
    qrCode: String
) {
    var isVisible by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = {
                isVisible = !isVisible
            }
        ) {
            Text(
                if (isVisible) "Hide My QR Code" else "Show My QR Code"
            )
        }

        if (isVisible) {
            Spacer(modifier = Modifier.height(16.dp))

            if (qrCode.isBlank()) {
                Text(
                    text = "This account does not have a QR code yet.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                val qrBitmap = remember(qrCode) {
                    createQrBitmap(qrCode)
                }

                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Participant check-in QR code",
                    modifier = Modifier.size(220.dp)
                )
            }
        }
    }
}

private fun createQrBitmap(
    value: String
): Bitmap {
    val size = 512
    val matrix = QRCodeWriter().encode(
        value,
        BarcodeFormat.QR_CODE,
        size,
        size
    )
    val bitmap = Bitmap.createBitmap(
        size,
        size,
        Bitmap.Config.RGB_565
    )

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(
                x,
                y,
                if (matrix[x, y]) Color.BLACK else Color.WHITE
            )
        }
    }

    return bitmap
}
