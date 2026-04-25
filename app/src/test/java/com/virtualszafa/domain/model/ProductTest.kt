package com.virtualszafa.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ProductTest {

    @Test
    fun createProduct_shouldGenerateIdAndSetDefaults() {
        val product = Product.create(
            name = "Test Shirt",
            brand = "Nike",
            size = "L",
            barcode = "1234567890123"
        )

        assertNotNull(product.id)
        assertEquals("Test Shirt", product.name)
        assertEquals("Nike", product.brand)
        assertEquals("L", product.size)
        assertEquals("1234567890123", product.barcode)
        assertEquals(null, product.aiRecognizedName)
        assertNotNull(product.dateAdded)
    }

    @Test
    fun createProduct_minimal_shouldUseDefaults() {
        val product = Product.create(name = "Minimal Item")

        assertEquals("M", product.size)
        assertEquals("", product.brand)
        assertEquals(null, product.barcode)
    }
}