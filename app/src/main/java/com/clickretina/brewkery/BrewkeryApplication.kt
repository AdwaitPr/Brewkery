package com.clickretina.brewkery

import android.app.Application
import com.clickretina.brewkery.di.AppContainer
import com.clickretina.brewkery.di.DefaultAppContainer

class BrewkeryApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
