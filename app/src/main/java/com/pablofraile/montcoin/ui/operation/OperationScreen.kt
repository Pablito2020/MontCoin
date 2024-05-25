package com.pablofraile.montcoin.ui.operation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.ui.LoadingAnimation

@Composable
fun OperationScreen(
    amount: String,
    amountIsValid: Boolean,
    card: CreditCardState,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onAmountChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AmountTextBox(amount = amount, isValid = amountIsValid, onAmountChange = onAmountChange)
        Spacer(modifier = Modifier.height(16.dp))
        ActionButton(cardState = card, onStart = onStart, onStop = onStop)
        if (card is CreditCardState.SearchingCard) {
            Spacer(modifier = Modifier.height(16.dp))
            LoadingAnimation(modifier = Modifier)
        }
    }
}

@Composable
fun AmountTextBox(amount: String, isValid: Boolean, onAmountChange: (String) -> Unit) {
    val keyboardConfig = KeyboardOptions(
        keyboardType = KeyboardType.Number,
    )
    OutlinedTextField(
        value = amount,
        isError = !isValid,
        onValueChange = onAmountChange,
        keyboardOptions = keyboardConfig,
        leadingIcon = {
            Icon(Icons.Filled.AttachMoney, contentDescription = "Amount of Money")
        })
}

@Composable
fun ActionButton(cardState: CreditCardState, onStart: () -> Unit, onStop: () -> Unit) {
    Button(
        onClick = {
            when (cardState) {
                is CreditCardState.StoppedSearching -> onStart()
                is CreditCardState.SearchingCard -> onStop()
                is CreditCardState.FoundCard -> {}
            }
        }
    ) {
        Text(
            text = when (cardState) {
                is CreditCardState.StoppedSearching -> "Start"
                is CreditCardState.SearchingCard -> "Stop"
                is CreditCardState.FoundCard -> "Stop"
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OperationScreenPreview() {
    OperationScreen(
        amount = "100",
        amountIsValid = true,
        card = CreditCardState.SearchingCard,
        onStart = {},
        onStop = {},
        onAmountChange = {}
    )
}