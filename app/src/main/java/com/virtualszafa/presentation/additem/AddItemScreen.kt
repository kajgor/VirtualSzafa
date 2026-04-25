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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    navController: NavController,
    barcode: String? = null,
    aiId: String? = null,
    prefilledName: String? = null,
    prefilledBrand: String? = null,
    prefilledSize: String? = null,
    prefilledColor: String? = null,
    viewModel: AddItemViewModel = hiltViewModel()
) {
    var name by remember(prefilledName, aiId) { mutableStateOf(prefilledName ?: aiId ?: "") }
    var brand by remember(prefilledBrand) { mutableStateOf(prefilledBrand ?: "") }
    var size by remember(prefilledSize) { mutableStateOf(prefilledSize ?: "M") }
    var color by remember(prefilledColor) { mutableStateOf(prefilledColor ?: "") }
    var notes by remember { mutableStateOf("") }

    val isRecognized = name.isNotBlank() || brand.isNotBlank() || size.isNotBlank()

    val sizeOptions = listOf("XXS", "XS", "S", "M", "L", "XL", "XXL", "XXXL")

    val handleBack: () -> Unit = { navController.popBackStack() }

    BackHandler(onBack = handleBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj do szafy") },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cofnij")
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
            if (!isRecognized) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Produkt nierozpoznany",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Produkt rozpoznany z etykiety",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nazwa produktu *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Marka") },
                modifier = Modifier.fillMaxWidth()
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = size,
                    onValueChange = {},
                    label = { Text("Rozmiar") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    sizeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                size = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Kolor") },
                modifier = Modifier.fillMaxWidth()
            )

            if (!barcode.isNullOrBlank()) {
                OutlinedTextField(
                    value = barcode,
                    onValueChange = {},
                    label = { Text("Kod kreskowy") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
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
                            barcode = barcode,
                            aiRecognizedName = if (isRecognized) "Rozpoznano z etykiety" else null
                        )
                        handleBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz w szafie")
            }
        }
    }
}