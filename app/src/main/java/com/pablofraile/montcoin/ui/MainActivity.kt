package com.pablofraile.montcoin.ui

import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.pablofraile.montcoin.MontCoinApplication
import com.pablofraile.montcoin.data.card.nfc.NfcCardRepository
import com.pablofraile.montcoin.utils.NfcActivityTemplate

class MainActivity : NfcActivityTemplate() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as MontCoinApplication).container
        setContent {
            MontCoinApp(container)
        }
    }

    override suspend fun onTagRead(tag: Tag?) {
        val nfc =
            ((application as MontCoinApplication).container.cardRepository as NfcCardRepository)
        nfc.send(tag)
    }

}