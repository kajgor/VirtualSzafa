package pl.virtualszafa.app.model

data class WardrobeItem(
    val id: String,
    val name: String,
    val category: String,
    val color: String,
    val season: String,
    val brand: String,
    val isForSale: Boolean = false,
)
