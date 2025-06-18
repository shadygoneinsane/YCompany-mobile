package com.ycompany.di

import android.app.Application
import com.ycompany.ui.ContextResourceProvider
import com.ycompany.ui.ResourceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {
    @Provides
    @Singleton
    fun provideResourceProvider(app: Application): ResourceProvider = ContextResourceProvider(app)
} 