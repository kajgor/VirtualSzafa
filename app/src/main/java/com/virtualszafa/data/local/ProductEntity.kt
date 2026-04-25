package com.virtualszafa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String,
    val size: String,
    val barcode: String?,
    val aiRecognizedName: String?,
    val imageUrl: String?,
    val dateAdded: Long
)