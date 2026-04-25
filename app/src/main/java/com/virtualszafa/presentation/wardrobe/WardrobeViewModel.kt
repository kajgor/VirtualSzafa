package com.virtualszafa.presentation.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualszafa.data.repository.WardrobeRepository
import com.virtualszafa.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    val products = wardrobeRepository.products.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog = _showAddDialog.asStateFlow()

    fun toggleSelection(id: String) {
        _selectedIds.value = if (_selectedIds.value.contains(id)) {
            _selectedIds.value - id
        } else {
            _selectedIds.value + id
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            wardrobeRepository.deleteByIds(_selectedIds.value)
            _selectedIds.value = emptySet()
        }
    }

    fun showAddManualDialog() {
        _showAddDialog.value = true
    }

    fun hideAddManualDialog() {
        _showAddDialog.value = false
    }

    fun addManualProduct(product: Product) {
        viewModelScope.launch {
            wardrobeRepository.addProduct(product)
            hideAddManualDialog()
        }
    }
}