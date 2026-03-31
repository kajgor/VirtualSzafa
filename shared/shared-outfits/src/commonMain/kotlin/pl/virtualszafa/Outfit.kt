package pl.virtualszafa

import kotlinx.serialization.Serializable

@Serializable
data class Outfit(
    val id: String,
    val title: String,
    val itemIds: List<String>,
)
