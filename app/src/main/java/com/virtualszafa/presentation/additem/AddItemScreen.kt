package com.virtualszafa.presentation.additem

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.virtualszafa.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    navController: NavController,
    barcode: String? = null,
    aiId: String? = null,
    viewModel: AddItemViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(aiId ?: "") }
    var brand by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("M") }
    var notes by remember { mutableStateOf("") }

    // Bezpośredni powrót do poprzedniego ekranu
    val handleBack: () -> Unit = {
        navController.popBackStack()
    }

    BackHandler(onBack = handleBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj do szafy") },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cofnij do skanowania"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nazwa produktu *") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = brand, onValueChange = { brand = it },
                label = { Text("Marka") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = size, onValueChange = { size = it },
                label = { Text("Rozmiar") }, modifier = Modifier.fillMaxWidth()
            )

            if (!barcode.isNullOrBlank()) {
                OutlinedTextField(
                    value = barcode, onValueChange = {},
                    label = { Text("Kod kreskowy / QR") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Dodatkowe uwagi") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.saveProduct(
                            name = name,
                            brand = brand,
                            size = size,
                            barcode = barcode
                        )
                        handleBack()   // jedno kliknięcie → powrót na kamerę
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz w szafie")
            }
        }
    }
}