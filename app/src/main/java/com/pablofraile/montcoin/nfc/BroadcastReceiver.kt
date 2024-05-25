package com.pablofraile.montcoin.nfc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Parcelable
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal fun <T : Parcelable> Intent.getParcelableCompatibility(key: String, type: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, type)
    } else {
        getParcelableExtra(key)
    }
}

data class NfcBroadcastReceiver(
    private val flow: MutableStateFlow<Tag?> = MutableStateFlow(null),
) : BroadcastReceiver() {

    val nfc: Flow<Tag?> = flow

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("NfcBroadcastReceiver", "onReceive")
        intent?.getParcelableCompatibility(NfcAdapter.EXTRA_TAG, Tag::class.java)
            ?.let { tag ->
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    flow.emit(tag)
                }
            }
    }
}