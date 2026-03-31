package pl.virtualszafa.app.data

import pl.virtualszafa.app.model.MarketplaceListing
import pl.virtualszafa.app.model.Outfit
import pl.virtualszafa.app.model.WardrobeItem

class FakeWardrobeRepository {
    fun getItems(): List<WardrobeItem> = listOf(
        WardrobeItem("1", "Biała koszula", "Góra", "Biały", "Cały rok", "Simple Wear", true),
        WardrobeItem("2", "Niebieskie jeansy", "Dół", "Niebieski", "Cały rok", "Denim Co", false),
        WardrobeItem("3", "Czarna marynarka", "Okrycie", "Czarny", "Jesień", "Urban Line", true),
        WardrobeItem("4", "Sneakersy", "Buty", "Biały", "Wiosna", "StreetStep", false),
        WardrobeItem("5", "Beżowy płaszcz", "Okrycie", "Beżowy", "Zima", "Nord Classic", true),
    )

    fun getOutfits(): List<Outfit> = listOf(
        Outfit("o1", "Smart casual", listOf("1", "2", "4"), "Praca"),
        Outfit("o2", "Wieczorne wyjście", listOf("1", "3", "4"), "Kolacja"),
    )

    fun getListings(): List<MarketplaceListing> = listOf(
        MarketplaceListing("l1", "1", "Vinted", "59 zł", "Opublikowane"),
        MarketplaceListing("l2", "3", "OLX", "129 zł", "Szkic"),
        MarketplaceListing("l3", "5", "Allegro", "199 zł", "Opublikowane"),
    )
}
