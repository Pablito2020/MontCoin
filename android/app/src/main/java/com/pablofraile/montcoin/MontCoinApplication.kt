package com.pablofraile.montcoin

import android.app.Application
import com.pablofraile.montcoin.data.AppContainer
import com.pablofraile.montcoin.data.AppContainerImpl

class MontCoinApplication : Application() {
    companion object {
        const val APP_URI = "https://www.pablofraile.net/montcoin"
    }

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}