package com.ycompany.ui.dashboard.products

import com.google.firebase.firestore.FirebaseFirestore
import com.ycompany.data.model.Product

class ProductsRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getProducts(callback: (List<Product>) -> Unit) {
        db.collection("products")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(Product::class.java) }
                callback(list)
            }
            .addOnFailureListener { callback(emptyList()) }
    }
}