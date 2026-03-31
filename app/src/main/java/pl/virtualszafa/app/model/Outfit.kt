package pl.virtualszafa.app.model

data class Outfit(
    val id: String,
    val title: String,
    val itemIds: List<String>,
    val occasion: String,
)
