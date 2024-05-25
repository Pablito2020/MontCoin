@file:OptIn(ExperimentalMaterial3Api::class)

package com.pablofraile.montcoin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pablofraile.montcoin.ui.operation.CreditCardState
import com.pablofraile.montcoin.ui.operation.CreditCardState.FoundCard
import com.pablofraile.montcoin.ui.operation.MontCoinOperationState
import com.pablofraile.montcoin.ui.operation.OperationScreen
import com.pablofraile.montcoin.ui.operation.OperationViewModel
import com.pablofraile.montcoin.ui.theme.MontCoinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MontCoinTheme {
                val model =
                    viewModel(factory = OperationViewModel.provideFactory()) as OperationViewModel
                val uiState by model.uiState.collectAsStateWithLifecycle()
                OperationScreen(
                    amount = uiState.amount.value,
                    amountIsValid = uiState.amount.isValid(),
                    card = uiState.card,
                    operation=uiState.operation,
                    onOperationErrorReaded = model::clearCurrentOperation,
                    onStart = { model.changeCardState(CreditCardState.SearchingCard) },
                    onStop = { model.changeCardState(CreditCardState.StoppedSearching) },
                    onAmountChange = model::changeAmount
                )
            }

        }
    }
}