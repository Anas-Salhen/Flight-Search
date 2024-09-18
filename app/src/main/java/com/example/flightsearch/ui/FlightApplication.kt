package com.example.flightsearch.ui

import android.app.Application
import com.example.flightsearch.data.AppContainer
import com.example.flightsearch.data.AppContainerImpl

class FlightApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}