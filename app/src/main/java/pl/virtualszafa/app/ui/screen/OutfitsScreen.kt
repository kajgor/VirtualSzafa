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
import pl.virtualszafa.app.model.Outfit
import pl.virtualszafa.app.model.WardrobeItem

@Composable
fun OutfitsScreen(
    modifier: Modifier = Modifier,
    outfits: List<Outfit>,
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
                text = "Stylizacje",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        items(outfits) { outfit ->
            val names = items.filter { it.id in outfit.itemIds }.joinToString { it.name }
            OutfitCard(outfit = outfit, itemNames = names)
        }
    }
}

@Composable
private fun OutfitCard(outfit: Outfit, itemNames: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = outfit.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Okazja: ${outfit.occasion}")
            Text(text = "Elementy: $itemNames")
        }
    }
}
