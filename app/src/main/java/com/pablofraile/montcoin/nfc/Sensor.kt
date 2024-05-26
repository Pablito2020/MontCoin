package com.pablofraile.montcoin.nfc

sealed class Sensor {
    data object Stopped : Sensor()
    data object Searching : Sensor()
}

