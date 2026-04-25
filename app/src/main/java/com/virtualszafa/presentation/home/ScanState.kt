package com.virtualszafa.presentation.home

sealed class ScanState {
    data object Idle : ScanState()
    data object Scanning : ScanState()
    data class BarcodeFound(val code: String) : ScanState()
    data class AIProductFound(val productId: String) : ScanState()
    data object AIProcessing : ScanState()
}