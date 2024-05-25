package com.pablofraile.montcoin.nfc

import com.pablofraile.montcoin.nfc.ReadTag

data class NfcValues(val currentTag: ReadTag? = null, val lastTag: ReadTag? = null) {
    fun isDifferentTag() = currentTag?.readOperation != lastTag?.readOperation
}