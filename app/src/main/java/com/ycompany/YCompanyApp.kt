package com.ycompany

import android.app.Application
import com.ycompany.di.AppComponent
import com.ycompany.di.DaggerAppComponent

class YCompanyApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        appComponent.inject(this)
    }
} 