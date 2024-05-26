package com.pablofraile.montcoin.ui.operation

sealed class NfcSensor {
    data object Stopped : NfcSensor()
    data object Searching : NfcSensor()
}

sealed class OperationResult {
    data object Success : OperationResult()
    class Error(val message: String) : OperationResult()
}