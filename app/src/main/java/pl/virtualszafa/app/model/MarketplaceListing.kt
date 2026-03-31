package pl.virtualszafa.app.model

data class MarketplaceListing(
    val id: String,
    val itemId: String,
    val channel: String,
    val price: String,
    val status: String,
)
