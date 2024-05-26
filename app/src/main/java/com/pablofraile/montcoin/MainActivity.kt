@file:OptIn(ExperimentalMaterial3Api::class)

package com.pablofraile.montcoin

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pablofraile.montcoin.nfc.NfcActivityTemplate
import com.pablofraile.montcoin.ui.operation.CreditCardState
import com.pablofraile.montcoin.ui.operation.OperationScreen
import com.pablofraile.montcoin.ui.operation.OperationViewModel
import com.pablofraile.montcoin.ui.theme.MontCoinTheme

class MainActivity : NfcActivityTemplate() {

    @SuppressLint("CoroutineCreationDuringComposition", "FlowOperatorInvokedInComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MontCoinTheme {
                val model =
                    viewModel(factory = OperationViewModel.provideFactory(this.data)) as OperationViewModel
                val card by model.cardState.collectAsStateWithLifecycle()
                val amount by model.amount.collectAsStateWithLifecycle()
                val operation by model.operationResult.collectAsStateWithLifecycle()
                val showResult by model.showOperationResult.collectAsStateWithLifecycle()
                val isDoingOperation by model.isDoingOperation.collectAsStateWithLifecycle()
                OperationScreen(
                    amount = amount.value,
                    amountIsValid = amount.isValid(),
                    card = card,
                    operation = operation,
                    onStart = {
                        model.changeCardState(CreditCardState.SearchingCard)
                    },
                    onStop = {
                        model.changeCardState(CreditCardState.StoppedSearching)
                    },
                    onAmountChange = model::changeAmount,
                    isDoingOperation = isDoingOperation,
                    showOperationResult = showResult,
                    onOperationErrorReaded = model::cleanOperationResult,
                    onOperationCorrectReaded = model::cleanOperationResult,
                )
            }

        }
    }

}