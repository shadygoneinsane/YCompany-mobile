package com.ycompany.ui.dashboard.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ycompany.data.model.Product

class ProductsViewModel : ViewModel() {

    private val repo = ProductsRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    fun loadProducts() {
        repo.getProducts { result ->
            _products.postValue(result)
        }
    }
}