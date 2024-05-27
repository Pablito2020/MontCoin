package com.pablofraile.montcoin.ui.operations

sealed class Sensor {
    data object Stopped : Sensor()
    data object Searching : Sensor()
}

