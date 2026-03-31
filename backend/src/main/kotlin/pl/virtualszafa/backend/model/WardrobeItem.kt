package pl.virtualszafa.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class WardrobeItem(
    val id: String,
    val name: String,
    val category: String,
    val color: String? = null,
    val size: String? = null,
)
