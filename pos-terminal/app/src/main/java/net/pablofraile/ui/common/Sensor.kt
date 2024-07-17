package net.pablofraile.ui.common

sealed class Sensor {
    data object Stopped : Sensor()
    data object Searching : Sensor()
}

