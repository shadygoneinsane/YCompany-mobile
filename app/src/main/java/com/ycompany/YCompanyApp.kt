package com.ycompany

import android.app.Application
import com.ycompany.di.AppComponent
import com.ycompany.di.DaggerAppComponent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class YCompanyApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        appComponent.inject(this)
        // Initialize Firebase Analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
        // Enable Crashlytics collection
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
} 