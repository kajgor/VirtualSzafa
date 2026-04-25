package com.virtualszafa.addtowardrobe

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.concurrent.Executors

/**
 * Ekran "Dodaj do szafy" z LIVE PREVIEW CameraX + jednym kliknięciem skanowania etykiety.
 * 
 * Zalety:
 * - Użytkownik widzi podgląd na żywo (jak w apce bankowej lub Instagramie)
 * - Po naciśnięciu "Skanuj" robi zdjęcie i automatycznie wypełnia pola
 * - Pełna integracja z ProductLabelRecognizer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToWardrobeScreenWithCameraX(
    viewModel: AddToWardrobeViewModel = viewModel(),
    onItemSaved: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // CameraX variables
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // === LIVE CAMERA PREVIEW ===
        if (hasCameraPermission) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            previewView = this
                            setupCamera(
                                previewView = this,
                                lifecycleOwner = lifecycleOwner,
                                onImageCaptureReady = { capture -> imageCapture = capture }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay z instrukcją
                Text(
                    text = "Najedź na etykietę produktu",
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                // Przycisk skanowania (duży, na dole podglądu)
                FloatingActionButton(
                    onClick = {
                        imageCapture?.let { capture ->
                            capturePhoto(capture, cameraExecutor) { bitmap ->
                                viewModel.onLabelPhotoCaptured(bitmap)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .size(72.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Skanuj etykietę",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        } else {
            // Brak uprawnień
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Potrzebujemy dostępu do kamery")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Przyznaj uprawnienia")
                    }
                }
            }
        }

        // === FORMULARZ (poniżej podglądu) ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.isScanning) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Rozpoznawanie etykiety...", style = MaterialTheme.typography.bodyMedium)
            }

            OutlinedTextField(
                value = uiState.productName,
                onValueChange = viewModel::updateProductName,
                label = { Text("Nazwa produktu") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.brand,
                onValueChange = viewModel::updateBrand,
                label = { Text("Marka") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.size,
                    onValueChange = viewModel::updateSize,
                    label = { Text("Rozmiar") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.color,
                    onValueChange = viewModel::updateColor,
                    label = { Text("Kolor") },
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.isLabelRecognized) {
                Text(
                    "✓ Rozpoznano: ${uiState.uniqueId}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Button(
                onClick = {
                    viewModel.saveToWardrobe { uniqueId -> onItemSaved(uniqueId) }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.productName.isNotBlank()
            ) {
                Text("Dodaj do szafy")
            }

            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ==================== POMOCNICZE FUNKCJE CAMERA X ====================

private fun setupCamera(
    previewView: PreviewView,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            onImageCaptureReady(imageCapture)
        } catch (e: Exception) {
            Log.e("CameraX", "Błąd wiązania kamery", e)
        }
    }, ContextCompat.getMainExecutor(previewView.context))
}

private fun capturePhoto(
    imageCapture: ImageCapture,
    executor: java.util.concurrent.Executor,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        java.io.File.createTempFile("label_", ".jpg")
    ).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Konwertuj plik na Bitmap
                val bitmap = BitmapFactory.decodeFile(output.savedUri?.path)
                    ?: return
                onPhotoCaptured(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Błąd robienia zdjęcia", exception)
            }
        }
    )
}