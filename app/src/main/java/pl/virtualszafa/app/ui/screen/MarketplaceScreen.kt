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
import pl.virtualszafa.app.model.MarketplaceListing
import pl.virtualszafa.app.model.WardrobeItem

@Composable
fun MarketplaceScreen(
    modifier: Modifier = Modifier,
    items: List<WardrobeItem>,
    listings: List<MarketplaceListing>,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Sprzedaż",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        items(listings) { listing ->
            val itemName = items.firstOrNull { it.id == listing.itemId }?.name.orEmpty()
            ListingCard(listing = listing, itemName = itemName)
        }
    }
}

@Composable
private fun ListingCard(listing: MarketplaceListing, itemName: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = itemName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Kanał: ${listing.channel}")
            Text(text = "Cena: ${listing.price}")
            Text(text = "Status: ${listing.status}")
        }
    }
}
