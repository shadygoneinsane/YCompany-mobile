package com.ycompany.ui.dashboard.products

import com.google.firebase.firestore.FirebaseFirestore
import com.ycompany.data.Constants
import com.ycompany.data.model.Product
import com.ycompany.data.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    suspend fun getProducts(): Resource<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection(Constants.COLLECTION_PRODUCTS).get().await()
            val products = snapshot.mapNotNull { document ->
                document.toObject(Product::class.java)?.let { product ->
                    product.copy(id = document.id)
                }
            }
            
            if (products.isEmpty()) {
                Resource.Success(emptyList())
            } else {
                Resource.Success(products)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }
    
    suspend fun getProductById(productId: String): Resource<Product> = withContext(Dispatchers.IO) {
        try {
            val document = firestore.collection(Constants.COLLECTION_PRODUCTS).document(productId).get().await()
            val product = document.toObject(Product::class.java)?.let { product ->
                product.copy(id = document.id)
            }
            
            if (product != null) {
                Resource.Success(product)
            } else {
                Resource.Error(Constants.ERROR_PRODUCT_NOT_FOUND)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }
}