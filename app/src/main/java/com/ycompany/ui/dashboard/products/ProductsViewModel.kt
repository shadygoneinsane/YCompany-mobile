package com.ycompany.ui.dashboard.products

import com.ycompany.data.Resource
import com.ycompany.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ycompany.data.model.Order
import com.ycompany.data.Constants
import com.google.firebase.Timestamp

@Singleton
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow<ProductsState>(ProductsState.Loading)
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    private val _effect = MutableStateFlow<ProductsEffect?>(null)
    val effect: StateFlow<ProductsEffect?> = _effect.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _cart = MutableStateFlow<List<com.ycompany.data.model.Product>>(emptyList())
    val cart = _cart.asStateFlow()

    init {
        loadProducts()
    }

    fun handleEvent(event: ProductsEvent) {
        when (event) {
            is ProductsEvent.LoadProducts -> loadProducts()
            is ProductsEvent.ProductClicked -> handleProductClick(event.product)
        }
    }

    private fun loadProducts() {
        launchWithExceptionHandler {
            _state.value = ProductsState.Loading
            
            when (val result = repository.getProducts()) {
                is Resource.Success -> {
                    val products = result.data
                    _state.value = if (products.isEmpty()) {
                        ProductsState.Empty
                    } else {
                        ProductsState.Success(products)
                    }
                }
                is Resource.Error -> {
                    _state.value = ProductsState.Error(result.message)
                }
                is Resource.Loading -> {
                    _state.value = ProductsState.Loading
                }
            }
        }
    }

    private fun handleProductClick(product: com.ycompany.data.model.Product) {
        _effect.value = ProductsEffect.NavigateToProductDetail(product)
    }

    fun clearEffect() {
        _effect.value = null
    }

    fun placeOrder(product: com.ycompany.data.model.Product, onResult: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "User not logged in")
            return
        }
        val order = Order(
            userId = user.uid,
            userName = user.displayName ?: "",
            userEmail = user.email ?: "",
            productId = product.id,
            productName = product.name,
            productPrice = product.price,
            productImageUrl = product.imageUrl,
            orderTime = Timestamp.now()
        )
        firestore.collection(Constants.COLLECTION_ORDERS)
            .add(order)
            .addOnSuccessListener { onResult(true, "Order placed successfully") }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage ?: "Order failed") }
    }

    fun addToCart(product: com.ycompany.data.model.Product) {
        _cart.value = _cart.value + product
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun placeAllOrders(onResult: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "User not logged in")
            return
        }
        val productsToOrder = _cart.value
        if (productsToOrder.isEmpty()) {
            onResult(false, "Cart is empty")
            return
        }
        var successCount = 0
        var failCount = 0
        var lastMessage = ""
        productsToOrder.forEach { product ->
            val order = Order(
                userId = user.uid,
                userName = user.displayName ?: "",
                userEmail = user.email ?: "",
                productId = product.id,
                productName = product.name,
                productPrice = product.price,
                productImageUrl = product.imageUrl,
                orderTime = Timestamp.now()
            )
            firestore.collection(Constants.COLLECTION_ORDERS)
                .add(order)
                .addOnSuccessListener {
                    successCount++
                    if (successCount + failCount == productsToOrder.size) {
                        clearCart()
                        onResult(true, "${successCount} order(s) placed successfully")
                    }
                }
                .addOnFailureListener { e ->
                    failCount++
                    lastMessage = e.localizedMessage ?: "Order failed"
                    if (successCount + failCount == productsToOrder.size) {
                        onResult(false, lastMessage)
                    }
                }
        }
    }
}