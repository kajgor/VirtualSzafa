package com.virtualszafa.presentation.wardrobe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.virtualszafa.domain.model.Product

@Composable
fun ProductListItem(
    product: Product,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = product.brand.ifBlank { "Brak marki" }, style = MaterialTheme.typography.titleMedium)
                Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Rozmiar: ${product.size} • ${product.barcode ?: "brak kodu"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}