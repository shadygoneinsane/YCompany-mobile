package com.ycompany.di

import android.app.Application
import com.ycompany.ui.ResourceProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        FirebaseModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    fun resourceProvider(): ResourceProvider
    fun viewModelFactory(): DaggerViewModelFactory
    fun inject(app: Application)
    fun inject(fragment: com.ycompany.ui.dashboard.products.ProductsFragment)
    fun inject(activity: com.ycompany.ui.SignInActivity)
    fun inject(activity: com.ycompany.ui.dashboard.DashboardActivity)
    fun inject(fragment: com.ycompany.ui.dashboard.orders.OrdersFragment)
    // Add inject methods for other Fragments, ViewModels, etc. as needed
    
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
} 