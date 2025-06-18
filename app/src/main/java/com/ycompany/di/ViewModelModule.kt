package com.ycompany.di

import dagger.Module
import dagger.Provides
import com.ycompany.data.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ycompany.ui.dashboard.products.ProductsViewModel
import com.ycompany.ui.SignInViewModel
import com.ycompany.ui.ResourceProvider
import com.ycompany.ui.dashboard.products.ProductsRepository

@Module
class ViewModelModule {
    @Provides
    fun provideProductsViewModel(repository: ProductsRepository): ProductsViewModel = ProductsViewModel(repository)

    @Provides
    fun provideSignInViewModel(
        auth: FirebaseAuth,
        db: FirebaseFirestore,
        resourceProvider: ResourceProvider,
        authRepository: AuthRepository
    ): SignInViewModel = SignInViewModel(auth, db, resourceProvider, authRepository)
} 