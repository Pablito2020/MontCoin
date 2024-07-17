package net.pablofraile

import android.app.Application
import net.pablofraile.data.AppContainer
import net.pablofraile.data.AppContainerImpl


class PosApplication : Application() {
    companion object {
        const val APP_URI = "https://www.pablofraile.net/montcoin"
    }

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}