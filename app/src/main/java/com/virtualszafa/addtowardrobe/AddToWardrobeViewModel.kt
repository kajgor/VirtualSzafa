package com.virtualszafa.addtowardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualszafa.labelrecognition.ProductLabelInfo
import com.virtualszafa.labelrecognition.ProductLabelRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel dla ekranu "Dodaj do szafy"
 * Automatycznie wypełnia pola po zeskanowaniu etykiety.
 */
@HiltViewModel
class AddToWardrobeViewModel @Inject constructor(
    private val labelRecognizer: ProductLabelRecognizer
) : ViewModel() {

    data class UiState(
        val productName: String = "",
        val brand: String = "",
        val size: String = "",
        val color: String = "",           // Kolor (nazwa czytelna dla użytkownika)
        val colorCode: String = "",       // Kod koloru (01X)
        val isScanning: Boolean = false,
        val errorMessage: String? = null,
        val isLabelRecognized: Boolean = false,
        val uniqueId: String = ""
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Wywoływane po zrobieniu zdjęcia etykiety (z kamery lub galerii)
     */
    fun onLabelPhotoCaptured(bitmap: android.graphics.Bitmap) {
        _uiState.update { it.copy(isScanning = true, errorMessage = null) }

        viewModelScope.launch {
            val result = labelRecognizer.recognizeLabel(bitmap)

            result.onSuccess { info ->
                if (info.isUniquelyIdentified()) {
                    _uiState.update {
                        it.copy(
                            productName = info.productName,
                            brand = info.brand,
                            size = info.size,
                            color = info.colorName,
                            colorCode = info.colorCode,
                            isScanning = false,
                            isLabelRecognized = true,
                            uniqueId = info.uniqueIdentifier,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isScanning = false,
                            errorMessage = "Nie udało się jednoznacznie rozpoznać etykiety. Spróbuj ponownie."
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        errorMessage = "Błąd rozpoznawania: ${error.message}"
                    )
                }
            }
        }
    }

    /**
     * Ręczne edytowanie pól (użytkownik może poprawić)
     */
    fun updateProductName(value: String) {
        _uiState.update { it.copy(productName = value) }
    }

    fun updateBrand(value: String) {
        _uiState.update { it.copy(brand = value) }
    }

    fun updateSize(value: String) {
        _uiState.update { it.copy(size = value) }
    }

    fun updateColor(value: String) {
        _uiState.update { it.copy(color = value) }
    }

    /**
     * Zapisanie ubrania do szafy (wywołuje createWardrobeItem z dokumentacji)
     */
    fun saveToWardrobe(onSuccess: (String) -> Unit) {
        val current = _uiState.value
        
        if (current.productName.isBlank() || current.brand.isBlank() || current.size.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Wypełnij wszystkie wymagane pola") }
            return
        }

        // W rzeczywistej aplikacji:
        // wardrobeRepository.createWardrobeItem(
        //     metadata = mapOf(
        //         "productName" to current.productName,
        //         "brand" to current.brand,
        //         "size" to current.size,
        //         "color" to current.color,
        //         "colorCode" to current.colorCode,
        //         "uniqueId" to current.uniqueId
        //     ),
        //     photos = listOf(...) // zdjęcie etykiety + zdjęcie ubrania
        // )
        
        // Na razie symulacja sukcesu
        onSuccess(current.uniqueId.ifEmpty { "manual-${System.currentTimeMillis()}" })
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}