package com.virtualszafa.presentation.home

import android.content.Context
import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualszafa.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState = _scanState.asStateFlow()

    // Kanał dla jednorazowych zdarzeń nawigacji
    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    sealed class NavigationEvent {
        data class NavigateToAddItem(val barcode: String?, val aiId: String?) : NavigationEvent()
    }

    init {
        viewModelScope.launch {
            scanRepository.scanResult.collect { result ->
                handleScanResult(result)
            }
        }
    }

    /**
     * Wspólna logika obsługi wyników skanowania (z kamery i z pliku)
     */
    private fun handleScanResult(result: ScanState) {
        _scanState.value = result

        when (result) {
            is ScanState.BarcodeFound -> {
                scanRepository.stopScan()
                viewModelScope.launch {
                    _navigationEvent.send(NavigationEvent.NavigateToAddItem(result.code, null))
                }
                _scanState.value = ScanState.Scanning
            }
            is ScanState.AIProductFound -> {
                scanRepository.stopScan()
                viewModelScope.launch {
                    _navigationEvent.send(NavigationEvent.NavigateToAddItem(null, result.productId))
                }
                _scanState.value = ScanState.Scanning
            }
            else -> {}
        }
    }

    /**
     * Toggle włącz/wyłącz skanowanie kamerą
     */
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

    /**
     * Natychmiast wyłącza tryb skanowania kamery
     * Używane przy powrocie z AddItemScreen i MyWardrobeScreen
     */
    fun stopScanning() {
        if (_scanState.value is ScanState.Scanning) {
            scanRepository.stopScan()
        }
        _scanState.value = ScanState.Idle
    }

    /** NOWA METODA – gwarantuje czysty stan przy każdym wejściu na HomeScreen */
    fun resetToIdle() {
        if (_scanState.value is ScanState.Scanning) {
            scanRepository.stopScan()
        }
        _scanState.value = ScanState.Idle
    }
}