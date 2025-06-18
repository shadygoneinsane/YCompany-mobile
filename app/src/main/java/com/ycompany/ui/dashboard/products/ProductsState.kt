package com.ycompany.ui.dashboard.products

import com.ycompany.data.model.Product

sealed class ProductsState {
    data object Loading : ProductsState()
    data class Success(val products: List<Product>) : ProductsState()
    data class Error(val message: String) : ProductsState()
    data object Empty : ProductsState()
}

sealed class ProductsEvent {
    data object LoadProducts : ProductsEvent()
    data class ProductClicked(val product: Product) : ProductsEvent()
}

sealed class ProductsEffect {
    data class NavigateToProductDetail(val product: Product) : ProductsEffect()
    data class ShowError(val message: String) : ProductsEffect()
} 