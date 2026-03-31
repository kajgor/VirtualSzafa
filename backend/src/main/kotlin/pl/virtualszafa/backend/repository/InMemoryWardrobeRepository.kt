package pl.virtualszafa.backend.repository

import pl.virtualszafa.backend.model.WardrobeItem

class InMemoryWardrobeRepository : WardrobeRepository {
    private val items = listOf(
        WardrobeItem(id = "1", name = "White Shirt", category = "tops", color = "white", size = "M"),
        WardrobeItem(id = "2", name = "Blue Jeans", category = "bottoms", color = "blue", size = "32"),
        WardrobeItem(id = "3", name = "Black Jacket", category = "outerwear", color = "black", size = "L"),
    )

    override fun getAll(): List<WardrobeItem> = items
}
