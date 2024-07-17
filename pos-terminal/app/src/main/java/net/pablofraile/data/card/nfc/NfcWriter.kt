package net.pablofraile.data.card.nfc

import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.Ndef
import java.io.IOException

object NfcTagWriter {

    fun write(tag: Tag?, message: NfcParcelable): Result<Unit> {
        if (tag == null) return Result.failure(NullPointerException("Tag is null"))
        return writeCorrectTag(tag, message)
    }

    private fun writeCorrectTag(tag: Tag, message: NfcParcelable): Result<Unit> {
        val ndef = Ndef.get(tag)
        if (!ndef.isWritable) return Result.failure(IllegalStateException("Tag is not writable"))
        return writeWriteableTag(ndef, message)
    }

    private fun writeWriteableTag(ndefTag: Ndef, message: NfcParcelable): Result<Unit> {
        return try {
            ndefTag.write(message)
            Result.success(Unit)
        } catch (_: FormatException) {
            Result.failure(IllegalArgumentException("NFC Message is not well formatted"))
        } catch (_: TagLostException) {
            Result.failure(IllegalStateException("Tag connection is lost. Please try again."))
        } catch (_: IOException) {
            Result.failure(IllegalStateException("Unknown error while writing to tag. Please try again."))
        }
    }
}

interface NfcParcelable {
    fun toMessage(): NdefMessage
}

fun Ndef.write(content: NfcParcelable) {
    connect()
    writeNdefMessage(content.toMessage())
    close()
}