package com.ycompany.ui.dashboard.orders

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ycompany.data.Constants
import com.ycompany.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    fun loadOrders() {
        val user = auth.currentUser ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val snapshot = firestore.collection(Constants.COLLECTION_ORDERS)
                .whereEqualTo("userId", user.uid)
                .get().await()
            val orderList = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
            _orders.value = orderList
        }
    }
} 