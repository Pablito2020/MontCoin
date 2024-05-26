package com.pablofraile.montcoin.nfc

import android.content.Intent
import android.nfc.Tag
import android.os.Build
import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

internal fun <T : Parcelable> Intent.getParcelableCompatibility(key: String, type: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, type)
    } else {
        getParcelableExtra(key)
    }
}

object NfcReader {

    private val _nfc: MutableStateFlow<ReadTag?> = MutableStateFlow(null)

    val nfc: Flow<ReadTag?> = _nfc

    suspend fun send(tag: Tag?) = tag?.let { _nfc.emit(ReadTag(tag, UUID.randomUUID())) }

}