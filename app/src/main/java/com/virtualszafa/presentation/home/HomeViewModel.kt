package com.virtualszafa.presentation.home

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualszafa.data.repository.ProductLabelRecognizer
import com.virtualszafa.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scanRepository: ScanRepository,
    private val labelRecognizer: ProductLabelRecognizer,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState = _scanState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    sealed class NavigationEvent {
        data class NavigateToAddItem(
            val name: String? = null,
            val brand: String? = null,
            val size: String? = null,
            val color: String? = null,
            val barcode: String? = null,
            val aiId: String? = null
        ) : NavigationEvent()
    }

    init {
        viewModelScope.launch {
            scanRepository.scanResult.collect { result ->
                handleScanResult(result)
            }
        }
    }

    private fun handleScanResult(result: ScanState) {
        _scanState.value = result

        when (result) {
            is ScanState.BarcodeFound -> {
                // Nie zatrzymujemy skanowania (unbindAll) przed zrobieniem zdjęcia,
                // bo unbindAll wyłączy ImageCapture.
                scanRepository.capturePhotoAsBitmap(context = context) { bitmap ->
                    // Teraz możemy zatrzymać skanowanie
                    scanRepository.stopScan()

                    if (bitmap != null) {
                        viewModelScope.launch {
                            try {
                                val labelResult = labelRecognizer.recognizeLabel(bitmap)
                                _navigationEvent.send(
                                    NavigationEvent.NavigateToAddItem(
                                        name = labelResult.productName,
                                        brand = labelResult.brand,
                                        size = labelResult.size,
                                        color = labelResult.color,
                                        barcode = result.code
                                    )
                                )
                            } catch (e: Exception) {
                                _navigationEvent.send(NavigationEvent.NavigateToAddItem(barcode = result.code))
                            }
                        }
                    } else {
                        viewModelScope.launch {
                            _navigationEvent.send(NavigationEvent.NavigateToAddItem(barcode = result.code))
                        }
                    }
                }
            }
            is ScanState.AIProductFound -> {
                scanRepository.stopScan()
                viewModelScope.launch {
                    _navigationEvent.send(NavigationEvent.NavigateToAddItem(aiId = result.productId))
                }
                _scanState.value = ScanState.Scanning
            }
            else -> {}
        }
    }

    fun toggleCameraScan() {
        val current = _scanState.value
        if (current is ScanState.Scanning) {
            scanRepository.stopScan()
            _scanState.value = ScanState.Idle
        } else {
            _scanState.value = ScanState.Scanning
        }
    }

    fun startCameraScan(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        scanRepository.startScan(context, lifecycleOwner, previewView)
    }

    fun analyzeImageFromGallery(context: Context, uri: Uri) {
        viewModelScope.launch {
            _scanState.value = ScanState.Scanning
            scanRepository.analyzeImageFromUri(context, uri) { result ->
                handleScanResult(result)
            }
        }
    }

    fun stopScanning() {
        if (_scanState.value is ScanState.Scanning) {
            scanRepository.stopScan()
        }
        _scanState.value = ScanState.Idle
    }

    fun resetToIdle() {
        if (_scanState.value is ScanState.Scanning) {
            scanRepository.stopScan()
        }
        _scanState.value = ScanState.Idle
    }
}