package pl.virtualszafa.app.viewmodel

import pl.virtualszafa.app.model.MarketplaceListing
import pl.virtualszafa.app.model.Outfit
import pl.virtualszafa.app.model.WardrobeItem

data class HomeUiState(
    val items: List<WardrobeItem> = emptyList(),
    val outfits: List<Outfit> = emptyList(),
    val listings: List<MarketplaceListing> = emptyList(),
    val selectedTab: AppTab = AppTab.WARDROBE,
)

enum class AppTab(val label: String) {
    WARDROBE("Garderoba"),
    OUTFITS("Stylizacje"),
    MARKETPLACE("Sprzedaż"),
}
