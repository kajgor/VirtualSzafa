package com.virtualszafa.data.repository

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.virtualszafa.presentation.home.ScanState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import java.io.IOException

@Singleton
class ScanRepository @Inject constructor() {

    private val _scanResult = MutableSharedFlow<ScanState>(extraBufferCapacity = 1)
    val scanResult: SharedFlow<ScanState> = _scanResult

    private var cameraProvider: ProcessCameraProvider? = null

    // ==================== Z KAMERY – CIĄGŁY LIVE SCANNING ====================
    fun startScan(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: androidx.camera.view.PreviewView
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            this.cameraProvider = cameraProvider

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()

            val scanner = BarcodeScanning.getClient(options)
            var lastBarcodeTime = 0L
            val barcodeCooldownMs = 800L // Throttle detections: improves performance (less ML calls) and stability (no duplicate nav)

            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy: ImageProxy ->
                @Suppress("ExperimentalGetImage")
                @OptIn(ExperimentalGetImage::class)
                val mediaImage = imageProxy.image

                if (mediaImage != null) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBarcodeTime < barcodeCooldownMs) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                    scanner.process(image)
                        .addOnSuccessListener { barcodes: List<Barcode> ->
                            if (barcodes.isNotEmpty()) {
                                val code = barcodes.first().rawValue ?: ""
                                lastBarcodeTime = System.currentTimeMillis()
                                _scanResult.tryEmit(ScanState.BarcodeFound(code))
                            }
                            // NIE emitujemy AIProcessing co klatkę – dzięki temu preview jest Ciągły!
                        }
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        }, ContextCompat.getMainExecutor(context))
    }

    // ==================== Z PLIKU (GALERIA) – bez zmian ====================
    fun analyzeImageFromUri(
        context: Context,
        uri: Uri,
        onResult: (ScanState) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, uri)

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes: List<Barcode> ->
                    if (barcodes.isNotEmpty()) {
                        val code = barcodes.first().rawValue ?: ""
                        onResult(ScanState.BarcodeFound(code))
                    } else {
                        onResult(ScanState.AIProcessing)
                    }
                }
                .addOnFailureListener {
                    onResult(ScanState.AIProcessing)
                }
        } catch (e: Exception) {
            onResult(ScanState.AIProcessing)
        }
    }

    fun stopScan() {
        cameraProvider?.unbindAll()
        cameraProvider = null
    }
}