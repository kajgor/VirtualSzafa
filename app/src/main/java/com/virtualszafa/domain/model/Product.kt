package com.virtualszafa.domain.model

import java.util.UUID

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val size: String,
    val barcode: String?,
    val aiRecognizedName: String?,
    val imageUrl: String?,
    val dateAdded: Long
) {
    companion object {
        /**
         * Tworzy nowy produkt z domyślnymi wartościami
         */
        fun create(
            name: String,
            brand: String = "",
            size: String = "M",
            barcode: String? = null,
            aiRecognizedName: String? = null,
            imageUrl: String? = null,
            dateAdded: Long = System.currentTimeMillis()
        ): Product = Product(
            id = UUID.randomUUID().toString(),
            name = name,
            brand = brand,
            size = size,
            barcode = barcode,
            aiRecognizedName = aiRecognizedName,
            imageUrl = imageUrl,
            dateAdded = dateAdded
        )
    }
}