package com.pablofraile.montcoin.data.card.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.provider.ContactsContract.Directory.PACKAGE_NAME
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