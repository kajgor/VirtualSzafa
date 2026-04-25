package com.virtualszafa.presentation.wardrobe

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWardrobeScreen(navController: NavController) {
    val viewModel: WardrobeViewModel = hiltViewModel()

    val products by viewModel.products.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moja szafa (${products.size})") },
                navigationIcon = {
                    // Zawsze wraca na ekran startowy
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
                actions = {
                    if (selectedIds.isNotEmpty()) {
                        IconButton(onClick = { viewModel.deleteSelected() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Wyrzuć z szafy", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddManualDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj ręcznie")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(products) { product ->
                ProductListItem(
                    product = product,
                    isSelected = selectedIds.contains(product.id),
                    onToggle = { viewModel.toggleSelection(product.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddItemManualDialog(
            onDismiss = { viewModel.hideAddManualDialog() },
            onSave = { viewModel.addManualProduct(it) }
        )
    }
}