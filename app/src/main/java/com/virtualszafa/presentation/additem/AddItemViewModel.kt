package com.virtualszafa.presentation.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualszafa.data.repository.WardrobeRepository
import com.virtualszafa.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    fun saveProduct(
        name: String,
        brand: String,
        size: String,
        barcode: String?,
        aiRecognizedName: String? = null
    ) {
        val product = Product.create(
            name = name,
            brand = brand,
            size = size,
            barcode = barcode,
            aiRecognizedName = aiRecognizedName
        )
        viewModelScope.launch {
            wardrobeRepository.addProduct(product)
        }
    }
}