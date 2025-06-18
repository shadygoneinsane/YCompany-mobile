package com.ycompany.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ycompany.ui.dashboard.products.ProductsViewModel
import com.ycompany.ui.SignInViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaggerViewModelFactory @Inject constructor(
    private val productsViewModel: ProductsViewModel,
    private val signInViewModel: SignInViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductsViewModel::class.java) -> productsViewModel as T
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> signInViewModel as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 