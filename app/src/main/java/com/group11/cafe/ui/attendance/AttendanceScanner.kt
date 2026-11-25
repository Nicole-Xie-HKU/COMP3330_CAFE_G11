
package com.group11.cafe.ui.attendance

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun AttendanceScanner(
    onQrScanned: (String) -> Unit,
    statusMessage: String? = null
) {
    val context = LocalContext.current

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            permissionGranted = granted
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Attendance Scanner",
            style = MaterialTheme.typography.headlineMedium
        )

        if (statusMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = statusMessage,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (permissionGranted) {
            val lifecycleOwner = context as? LifecycleOwner

            if (lifecycleOwner != null) {
                CameraPreview(
                    lifecycleOwner = lifecycleOwner,
                    onQrScanned = onQrScanned,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                Text("Camera lifecycle is unavailable.")
            }
        } else {
            Text("Camera permission is required to scan QR codes.")

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    permissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            ) {
                Text("Allow Camera")
            }
        }
    }
}

@Composable
private fun CameraPreview(
    lifecycleOwner: LifecycleOwner,
    onQrScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val latestCallback by rememberUpdatedState(onQrScanned)

    val executor = remember {
        Executors.newSingleThreadExecutor()
    }

    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        BarcodeScanning.getClient(options)
    }

    val hasScanned = remember {
        AtomicBoolean(false)
    }

    DisposableEffect(Unit) {
        onDispose {
            scanner.close()
            executor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->

            val previewView = PreviewView(viewContext)

            previewView.scaleType =
                PreviewView.ScaleType.FILL_CENTER

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(viewContext)

            cameraProviderFuture.addListener({

                val cameraProvider =
                    cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(
                            previewView.surfaceProvider
                        )
                    }

                val imageAnalysis =
                    ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    if (hasScanned.get()) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val mediaImage = imageProxy.image

                    if (mediaImage == null) {
                        imageProxy.close()
                    } else {

                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->

                                val qrValue = barcodes
                                    .firstOrNull()
                                    ?.rawValue

                                if (
                                    qrValue != null &&
                                    hasScanned.compareAndSet(
                                        false,
                                        true
                                    )
                                ) {
                                    latestCallback(qrValue)
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    }
                }

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )

                } catch (exception: Exception) {
                    exception.printStackTrace()
                }

            }, ContextCompat.getMainExecutor(viewContext))

            previewView
        }
    )
}
