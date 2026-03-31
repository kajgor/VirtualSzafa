package pl.virtualszafa.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.virtualszafa.app.model.WardrobeItem

@Composable
fun WardrobeScreen(
    modifier: Modifier = Modifier,
    items: List<WardrobeItem>,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Moja garderoba",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        items(items) { item ->
            WardrobeCard(item)
        }
    }
}

@Composable
private fun WardrobeCard(item: WardrobeItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Kategoria: ${item.category}")
            Text(text = "Kolor: ${item.color} • Sezon: ${item.season}")
            Text(text = "Marka: ${item.brand}")
            Text(text = if (item.isForSale) "Status: gotowe do sprzedaży" else "Status: w garderobie")
        }
    }
}
