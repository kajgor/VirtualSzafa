package pl.virtualszafa.backend.repository

import pl.virtualszafa.backend.model.WardrobeItem

interface WardrobeRepository {
    fun getAll(): List<WardrobeItem>
}
