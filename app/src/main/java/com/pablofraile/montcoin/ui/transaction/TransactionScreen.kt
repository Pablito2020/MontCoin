package com.pablofraile.montcoin.ui.transaction

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransactionScreen(
    amount: String,
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
        verticalArrangement = Arrangement.Center
    ) {
        AmountTextBox(amount = amount, onAmountChange = onAmountChange)
        Spacer(modifier = Modifier.height(16.dp))
        ActionButton(cardState = card, onStart = onStart, onStop = onStop)
        if (card is CreditCardState.SearchingCard) {
            Spacer(modifier = Modifier.height(16.dp))
            LoadingAnimation()
        }
    }
}

@Composable
fun AmountTextBox(amount: String, onAmountChange: (String) -> Unit) {
    BasicTextField(
        value = amount,
        onValueChange = onAmountChange,
        textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
        modifier = Modifier
            .border(1.dp, Color.Gray)
            .padding(8.dp)
            .fillMaxWidth()
    )
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

@Composable
fun LoadingAnimation() {
    CircularProgressIndicator()
}