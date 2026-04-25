package com.virtualszafa.data.repository

import com.virtualszafa.data.local.ProductDao
import com.virtualszafa.data.local.ProductEntity
import com.virtualszafa.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WardrobeRepository @Inject constructor(
    private val productDao: ProductDao
) {

    val products: Flow<List<Product>> = productDao.getAllProductsFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getAllProducts(): List<Product> = productDao.getAllProducts().map { it.toDomain() }

    suspend fun addProduct(product: Product) {
        productDao.insert(product.toEntity())
    }

    suspend fun deleteByIds(ids: Set<String>) {
        productDao.deleteByIds(ids)
    }

    private fun ProductEntity.toDomain(): Product = Product(
        id = id,
        name = name,
        brand = brand,
        size = size,
        barcode = barcode,
        aiRecognizedName = aiRecognizedName,
        imageUrl = imageUrl,
        dateAdded = dateAdded
    )

    private fun Product.toEntity(): ProductEntity = ProductEntity(
        id = id,
        name = name,
        brand = brand,
        size = size,
        barcode = barcode,
        aiRecognizedName = aiRecognizedName,
        imageUrl = imageUrl,
        dateAdded = dateAdded
    )
}