package com.virtualszafa.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.virtualszafa.presentation.home.ScanState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor() {

    private val _scanResult = MutableSharedFlow<ScanState>(extraBufferCapacity = 1)
    val scanResult: SharedFlow<ScanState> = _scanResult

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var isScanning = false

    fun startScan(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        if (isScanning) return
        isScanning = true

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
            val barcodeCooldownMs = 800L

            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy: ImageProxy ->
                @Suppress("DEPRECATION")
                val mediaImage = imageProxy.image

                if (mediaImage != null) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBarcodeTime < barcodeCooldownMs) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                val code = barcodes.first().rawValue ?: ""
                                lastBarcodeTime = System.currentTimeMillis()
                                _scanResult.tryEmit(ScanState.BarcodeFound(code))
                            }
                        }
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                }
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer,
                    imageCapture
                )
            } catch (e: Exception) {
                _scanResult.tryEmit(ScanState.Idle)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhotoAsBitmap(
        context: Context,
        onResult: (Bitmap?) -> Unit
    ) {
        val capture = imageCapture ?: run {
            onResult(null)
            return
        }

        val outputFile = File.createTempFile("label_capture_", ".jpg", context.cacheDir)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                        onResult(bitmap)
                        outputFile.delete()
                    } catch (e: Exception) {
                        onResult(null)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    onResult(null)
                }
            }
        )
    }

    fun analyzeImageFromUri(
        context: Context,
        uri: Uri,
        onResult: (ScanState) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
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
        imageCapture = null
        isScanning = false
    }
}