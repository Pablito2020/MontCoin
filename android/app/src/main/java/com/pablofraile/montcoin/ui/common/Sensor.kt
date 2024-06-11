package com.pablofraile.montcoin.ui.common

sealed class Sensor {
    data object Stopped : Sensor()
    data object Searching : Sensor()
}

