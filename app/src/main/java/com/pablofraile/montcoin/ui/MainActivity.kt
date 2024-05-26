package com.pablofraile.montcoin.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.pablofraile.montcoin.nfc.NfcActivityTemplate
import com.pablofraile.montcoin.ui.MontCoinApp

class MainActivity : NfcActivityTemplate() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MontCoinApp(data = data)
        }
    }

}