package com.virtualszafa.presentation.home

import com.virtualszafa.labelrecognition.ProductLabelInfo

sealed class ScanState {
    data object Idle : ScanState()
    data object Scanning : ScanState()
    data class BarcodeFound(val code: String) : ScanState()
    data class AIProductFound(val productId: String) : ScanState()
    data class LabelRecognized(val info: ProductLabelInfo) : ScanState()
    data object AIProcessing : ScanState()
}