package com.danielefavaro.waround

import android.app.Application
import com.danielefavaro.waround.di.AppComponent
import com.danielefavaro.waround.di.DaggerAppComponent

class WaroundApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }
}