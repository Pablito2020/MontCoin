package com.pablofraile.montcoin.data.card.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User


fun User.toNfcTag(): NfcParcelable {
    val parcelable = object : NfcParcelable {
        override fun toMessage(): NdefMessage {
            return NdefMessage(
                arrayOf(
                    NdefRecord.createMime(
                        "application/$PACKAGE_NAME",
                        this@toNfcTag.id.toByteArray()
                    ),
                ),
            )
        }
    }
    return parcelable
}

fun Intent.toUserId(): Result<Id> {
    try {
        val parcelables = this.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        with(parcelables) {
            val inNdefMessage = this?.get(0) as NdefMessage
            val inNdefRecord = inNdefMessage.records
            val identifier = String(inNdefRecord[0].payload)
            return Result.success(Id(identifier))
        }
    } catch (e: Exception) {
        return Result.failure(IllegalAccessError("Couldn't read id from NFC tag. Are you sure it's a MontCoin tag?"))
    }
}