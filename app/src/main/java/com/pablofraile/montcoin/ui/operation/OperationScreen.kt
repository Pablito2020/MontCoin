package com.pablofraile.montcoin.ui.operation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.ui.LoadingAnimation
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun OperationScreen(
    amount: String,
    amountIsValid: Boolean,
    card: CreditCardState,
    operation: MontCoinOperationState?,
    onOperationErrorReaded: () -> Unit,
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
        ShowOperationUi(operation = operation, onOperationErrorReaded = onOperationErrorReaded)
        AmountTextBox(
            amount = amount,
            isValid = amountIsValid,
            onAmountChange = onAmountChange,
            onDone = onStart
        )
        Spacer(modifier = Modifier.height(16.dp))
        ActionButton(cardState = card, onStart = onStart, onStop = onStop)
        if (card is CreditCardState.SearchingCard) {
            Spacer(modifier = Modifier.height(16.dp))
            LoadingAnimation(modifier = Modifier)
        }
    }
}

@Composable
fun ShowOperationUi(
    operation: MontCoinOperationState?,
    onOperationErrorReaded: () -> Unit
) {
    if (operation == null) return
    when (operation) {
        MontCoinOperationState.DoingIt -> ShowOperationDoingDialog()
        is MontCoinOperationState.Error -> ErrorOperationDialog(
            operation = operation,
            onOperationErrorReaded = onOperationErrorReaded
        )

        MontCoinOperationState.Success -> ShowCorrectOperationSnackBar()
    }
}

@Composable
fun ShowCorrectOperationSnackBar() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = snackbarHostState) {
        scope.launch {
            snackbarHostState.showSnackbar(
                "Operation Done correctly!"
            )
        }
    }
}

@Composable
fun ShowOperationDoingDialog(
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        title = {
            Text("Doing Operation...")
        },
        text = @Composable {
            CircularProgressIndicator()
        })
}

@Composable
fun ErrorOperationDialog(
    operation: MontCoinOperationState.Error,
    onOperationErrorReaded: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = @Composable {
            TextButton(onClick = onOperationErrorReaded) {
                Text("Ok")
            }
        },
        dismissButton = {},
        icon = @Composable {
            Icon(Icons.Filled.Warning, contentDescription = "Error")
        },
        title = @Composable {
            Text("Couldn't do operation!")
        },
        text = @Composable {
            Text("Error Message: ${operation.message}")
        })
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AmountTextBox(
    amount: String,
    isValid: Boolean,
    onAmountChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val keyboardConfig = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Go
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardActions = KeyboardActions(onGo = {
        keyboardController?.hide()
        onDone()
    })
    OutlinedTextField(
        value = amount,
        isError = !isValid,
        onValueChange = onAmountChange,
        keyboardOptions = keyboardConfig,
        keyboardActions = keyboardActions,
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun OperationScreenPreview() {
    OperationScreen(
        amount = "100",
        amountIsValid = true,
        operation = MontCoinOperationState.Success,
        card = CreditCardState.FoundCard("123"),
        onOperationErrorReaded = {},
        onStart = {},
        onStop = {},
        onAmountChange = {}
    )
}