package com.ycompany.di

import com.ycompany.ui.dashboard.products.ProductsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.ycompany.data.AuthRepository

@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideProductsRepository(firestore: FirebaseFirestore): ProductsRepository = ProductsRepository(firestore)

    @Provides
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepository(auth)
    // Add other repositories here
} 